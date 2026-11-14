package com.companion.skills.MiningBot;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

import static com.companion.global.MiningBotOldPath;

public class FindPath {
    public static final Set<Block> AvoidBlock =new HashSet<>();
    static {
        AvoidBlock.add(Blocks.LAVA);
        AvoidBlock.add(Blocks.WATER);
        AvoidBlock.add(Blocks.AIR);
        AvoidBlock.add(Blocks.VOID_AIR);
        AvoidBlock.add(Blocks.FIRE);
        AvoidBlock.add(Blocks.GRAVEL);
        AvoidBlock.add(Blocks.MAGMA_BLOCK);
        AvoidBlock.add(Blocks.SAND);
        AvoidBlock.add(Blocks.BEDROCK);
    };



    private static final BlockPos[] offsets = {
            BlockPos.ORIGIN,          // 自身
            new BlockPos(0,1,0),      // 上
            new BlockPos(0,2,0),      // 上上
            new BlockPos(0,-1,0),     // 下
            new BlockPos(1,0,0),      // 东
            new BlockPos(-1,0,0),     // 西
            new BlockPos(0,0,1),      // 南
            new BlockPos(0,0,-1),     // 北
            new BlockPos(1,1,0),      // 东上
            new BlockPos(-1,1,0),     // 西上
            new BlockPos(0,1,1),      // 南上
            new BlockPos(0,1,-1)      // 北上
    };

    // 修改1：恢复UP方向，允许向上1格
    private static final Direction[] DIRECTIONS = {
            Direction.UP, Direction.DOWN,
            Direction.NORTH, Direction.SOUTH,
            Direction.EAST, Direction.WEST
    };
    private static final int MAX_SEARCH_RADIUS = 200;
    private static final int ADJUSTMENT_RADIUS = 5;

    public static List<BlockPos> findPath(ClientWorld world, BlockPos start, BlockPos target) {
        if (start.equals(target)) {
            List<BlockPos> list = new ArrayList<>();
            list.add(start);
            return list;
        }
        if (world == null || start == null || target == null) return new ArrayList<>();

        BlockPos adjustedStart = adjustBlockPosition(world, start);
        BlockPos adjustedTarget = adjustBlockPosition(world, target);
        if (adjustedStart == null || adjustedTarget == null) return new ArrayList<>();

        if (!isPassable(world, adjustedStart) || !isPassable(world, adjustedTarget))
            return new ArrayList<>();

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

    private static boolean isPassable(ClientWorld world, BlockPos pos) {
        if (world == null || pos == null) return false;

        for (BlockPos offset : offsets) {
            BlockPos checkPos = pos.add(offset);
            if(MiningBotOldPath.contains(checkPos) && world.getBlockState(checkPos).isAir()){
                continue;
            }
            if (AvoidBlock.contains(world.getBlockState(checkPos).getBlock())) {
                return false;
            }
        }

        return true;
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

                // 修改2：添加高度差检查，禁止向上≥2格
                int yDiff = neighborPos.getY() - current.pos.getY();
                if (yDiff > 1) continue;  // 只允许向上1格或更少

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
        return new ArrayList<>();
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