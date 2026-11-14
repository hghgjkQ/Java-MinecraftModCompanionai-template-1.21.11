package com.companion.python;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.*;

public class find {
    private static final Direction[] DIRECTIONS = {
            Direction.UP, Direction.DOWN,
            Direction.NORTH, Direction.SOUTH,
            Direction.EAST, Direction.WEST
    };
    private static final int MAX_SEARCH_RADIUS = 200;
    private static final int ADJUSTMENT_RADIUS = 3;
    // 转向惩罚值：可根据需要调整，值越大越优先选择少拐弯的路径
    private static final int TURN_PENALTY = 5;

    public static List<BlockPos> findPath(ClientWorld world, BlockPos start, BlockPos target) {
        if (start.equals(target)) return Collections.singletonList(start);
        if (world == null || start == null || target == null) return Collections.emptyList();

        BlockPos adjustedStart = adjustBlockPosition(world, start);
        BlockPos adjustedTarget = adjustBlockPosition(world, target);
        if (adjustedStart == null || adjustedTarget == null) return Collections.emptyList();

        if (!isPassable(world, adjustedStart) || !isPassable(world, adjustedTarget))
            return Collections.emptyList();

        List<BlockPos> fullPath = aStarSearch(world, adjustedStart, adjustedTarget);
        if (fullPath.isEmpty()) return Collections.emptyList();
        return extractTurningPoints(fullPath);
    }

    private static BlockPos adjustBlockPosition(ClientWorld world, BlockPos original) {
        if (isPassable(world, original)) return original;

        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int dx = -ADJUSTMENT_RADIUS; dx <= ADJUSTMENT_RADIUS; dx++) {
            for (int dy = -ADJUSTMENT_RADIUS; dy <= ADJUSTMENT_RADIUS; dy++) {
                for (int dz = -ADJUSTMENT_RADIUS; dz <= ADJUSTMENT_RADIUS; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos candidate = original.add(dx, dy, dz);
                    if (isPassable(world, candidate)) {
                        int dist = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = candidate;
                        }
                    }
                }
            }
        }
        return best;
    }

    /**
     * 核心修改：判定方块可通行时，要求当前方块和其正上方一格都为空
     */
    private static boolean isPassable(ClientWorld world, BlockPos pos) {
        if (world == null || pos == null) return false;

        // 检查当前方块是否为空
        BlockState currentState = world.getBlockState(pos);
        VoxelShape currentShape = currentState.getCollisionShape(world, pos);
        if (!currentShape.isEmpty()) return false;

        // 检查当前方块正上方一格是否为空（适配2格高的玩家）
        BlockPos upPos = pos.up(); // 等价于 pos.offset(Direction.UP)
        BlockState upState = world.getBlockState(upPos);
        VoxelShape upShape = upState.getCollisionShape(world, upPos);

        return upShape.isEmpty();
    }

    private static List<BlockPos> aStarSearch(ClientWorld world, BlockPos start, BlockPos target) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<BlockPos, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, heuristic(start, target));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        int targetX = target.getX(), targetY = target.getY(), targetZ = target.getZ();
        int processed = 0, maxNodes = 50000;

        while (!openSet.isEmpty() && processed++ < maxNodes) {
            Node current = openSet.poll();

            if (current.pos.getX() == targetX && current.pos.getY() == targetY && current.pos.getZ() == targetZ) {
                return reconstructPath(current);
            }

            if (manhattanDistance(current.pos, target) > MAX_SEARCH_RADIUS) continue;

            for (Direction dir : DIRECTIONS) {
                BlockPos neighborPos = current.pos.offset(dir);
                if (!isPassable(world, neighborPos)) continue;

                // 核心修改：计算基础代价 + 转向惩罚
                int turnPenalty = getTurnPenalty(current, dir);
                int tentativeG = current.g + 1 + turnPenalty;

                Node neighbor = allNodes.get(neighborPos);
                if (neighbor == null) {
                    neighbor = new Node(neighborPos, current, tentativeG, heuristic(neighborPos, target));
                    allNodes.put(neighborPos, neighbor);
                    openSet.add(neighbor);
                } else if (tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.f = neighbor.g + neighbor.h;
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * 计算转向惩罚：如果当前移动方向与上一步方向不同，增加惩罚值
     */
    private static int getTurnPenalty(Node currentNode, Direction currentDir) {
        // 起点没有父节点，无转向惩罚
        if (currentNode.parent == null) {
            return 0;
        }
        // 获取上一步的移动方向
        Direction prevDir = getDirection(currentNode.parent.pos, currentNode.pos);
        // 方向不同则增加惩罚，相同则无惩罚
        return !prevDir.equals(currentDir) ? TURN_PENALTY : 0;
    }

    private static int manhattanDistance(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    private static int heuristic(BlockPos a, BlockPos b) {
        return manhattanDistance(a, b);
    }

    private static List<BlockPos> reconstructPath(Node end) {
        LinkedList<BlockPos> path = new LinkedList<>();
        for (Node n = end; n != null; n = n.parent) path.addFirst(n.pos);
        return path;
    }

    /**
     * 从完整路径中提取拐弯点（起点、终点以及所有方向变化的点）
     */
    private static List<BlockPos> extractTurningPoints(List<BlockPos> path) {
        if (path.isEmpty()) return Collections.emptyList();
        if (path.size() <= 2) return new ArrayList<>(path); // 一个或两个点直接返回

        List<BlockPos> turningPoints = new ArrayList<>();
        turningPoints.add(path.get(0)); // 起点

        Direction prevDir = getDirection(path.get(0), path.get(1));
        for (int i = 1; i < path.size() - 1; i++) {
            Direction currDir = getDirection(path.get(i), path.get(i + 1));
            if (!currDir.equals(prevDir)) {
                turningPoints.add(path.get(i)); // 方向变化点
            }
            prevDir = currDir;
        }
        turningPoints.add(path.get(path.size() - 1)); // 终点
        return turningPoints;
    }

    /**
     * 获取从 from 到 to 的方向（要求两点相邻）
     */
    private static Direction getDirection(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        if (dx == 1 && dy == 0 && dz == 0) return Direction.EAST;
        if (dx == -1 && dy == 0 && dz == 0) return Direction.WEST;
        if (dy == 1 && dx == 0 && dz == 0) return Direction.UP;
        if (dy == -1 && dx == 0 && dz == 0) return Direction.DOWN;
        if (dz == 1 && dx == 0 && dy == 0) return Direction.SOUTH;
        if (dz == -1 && dx == 0 && dy == 0) return Direction.NORTH;
        throw new IllegalArgumentException("Points are not adjacent: " + from + " -> " + to);
    }

    private static class Node {
        BlockPos pos;
        Node parent;
        int g, h, f;

        Node(BlockPos pos, Node parent, int g, int h) {
            this.pos = pos;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            return pos.equals(((Node) o).pos);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }
}