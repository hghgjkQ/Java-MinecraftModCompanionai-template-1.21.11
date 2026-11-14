package com.companion.skills;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.*;

public class findpathcut {
    private static final Direction[] DIRECTIONS = {
            Direction.UP, Direction.DOWN,
            Direction.NORTH, Direction.SOUTH,
            Direction.EAST, Direction.WEST
    };
    private static final int MAX_SEARCH_RADIUS = 200;
    private static final int ADJUSTMENT_RADIUS = 5;

    public static List<BlockPos> findPath(ClientWorld world, BlockPos start, BlockPos target) {
        if (start.equals(target)) return Collections.singletonList(start);
        if (world == null || start == null || target == null) return Collections.emptyList();

        BlockPos adjustedStart = adjustBlockPosition(world, start);
        BlockPos adjustedTarget = adjustBlockPosition(world, target);
        if (adjustedStart == null || adjustedTarget == null) return Collections.emptyList();

        if (!isPassable(world, adjustedStart) || !isPassable(world, adjustedTarget))
            return Collections.emptyList();

        return aStarSearch(world, adjustedStart, adjustedTarget);
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

                int tentativeG = current.g + 1;
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

    private static int manhattanDistance(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    // 修改启发函数为曼哈顿距离，确保找到最短路径（节点最少）
    private static int heuristic(BlockPos a, BlockPos b) {
        return manhattanDistance(a, b);
    }

    private static List<BlockPos> reconstructPath(Node end) {
        LinkedList<BlockPos> path = new LinkedList<>();
        for (Node n = end; n != null; n = n.parent) path.addFirst(n.pos);
        return path;
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