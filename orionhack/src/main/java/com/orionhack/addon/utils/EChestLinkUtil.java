package com.orionhack.addon.utils;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EChestLinkUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static BlockPos targetEC = null;
    private static int tickCounter = 0;
    private static boolean opening = false;

    public static boolean open(Vec3d coords) {
        if (mc.player == null || mc.world == null) return false;
        BlockPos pos = new BlockPos((int) coords.x, (int) coords.y - 1, (int) coords.z); // adjust to EC base
        targetEC = pos;
        opening = true;
        tickCounter = 0;
        return true;
    }

    public static void tick() {
        if (!opening || mc.player == null || targetEC == null) return;

        Vec3d targetPos = new Vec3d(targetEC.getX() + 0.5, targetEC.getY() + 2, targetEC.getZ() + 0.5);
        TPUtil.tpTo(targetPos);

        if (isAtPosition(targetPos)) {
            tickCounter++;
            if (tickCounter >= 3) {
                openEC(targetEC);
                opening = false;
                targetEC = null;
            }
        } else {
            tickCounter = 0;
        }
    }

    private static boolean isAtPosition(Vec3d target) {
        if (mc.player == null) return false;
        return new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()).squaredDistanceTo(target) < 0.01;
    }

    private static void openEC(BlockPos pos) {
        if (mc.player == null) return;
        for (int i = 0; i < 8; i++) {
            BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
            PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0);
            mc.player.networkHandler.sendPacket(packet);
            if (mc.interactionManager != null) mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        }
    }

    public static boolean isOpening() {
        return opening;
    }

    public static Vec3d findNearestEC(double maxRange) {
        if (mc.world == null || mc.player == null) return null;
        Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        BlockPos playerBlockPos = mc.player.getBlockPos();
        Vec3d closest = null;
        double closestDist = Double.MAX_VALUE;
        int range = (int) Math.min(32, Math.ceil(maxRange)); // render safe
        for (int x = -range; x <= range; x++) {
            for (int y = -range / 2; y <= range / 2; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos check = playerBlockPos.add(x, y, z);
                    if (mc.world.getBlockState(check).getBlock() == Blocks.ENDER_CHEST) {
                        Vec3d ecPos = new Vec3d(check.getX() + 0.5, check.getY() + 1, check.getZ() + 0.5);
                        double distSq = playerPos.squaredDistanceTo(ecPos);
                        if (distSq < closestDist && Math.sqrt(distSq) <= maxRange) {
                            closestDist = distSq;
                            closest = ecPos;
                        }
                    }
                }
            }
        }
        return closest;
    }
}
