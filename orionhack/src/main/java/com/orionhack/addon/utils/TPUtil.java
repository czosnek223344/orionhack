package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;

public class TPUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    private static final double MAX_STEP = 10.0;

    public static void tpTo(Vec3d target) {
        if (mc.player == null || mc.world == null) return;

        Vec3d start = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        
        if (start.distanceTo(target) <= 0.1) return;

        List<Vec3d> waypoints = calculateSmartPath(start, target);

        for (int i = 1; i < waypoints.size(); i++) {
            Vec3d from = waypoints.get(i - 1);
            Vec3d to = waypoints.get(i);
            sendSteps(from, to, i == waypoints.size() - 1);
        }

        mc.player.setPosition(target);
    }

    public static void preparePath(Vec3d target) {
        tpTo(target);
    }

    private static void sendSteps(Vec3d from, Vec3d to, boolean isFinalWaypoint) {
        double dist = from.distanceTo(to);
        if (dist <= 0.01) return;

        int steps = (int) Math.ceil(dist / MAX_STEP);
        double stepX = (to.x - from.x) / steps;
        double stepY = (to.y - from.y) / steps;
        double stepZ = (to.z - from.z) / steps;

        Vec3d current = from;

        for (int i = 0; i < steps; i++) {
            current = current.add(stepX, stepY, stepZ);
            
            boolean onGround = isFinalWaypoint && (i == steps - 1) && mc.player.isOnGround();
            sendPositionPacket(current, onGround);
        }
    }

    private static List<Vec3d> calculateSmartPath(Vec3d start, Vec3d target) {
        List<Vec3d> path = new ArrayList<>();
        path.add(start);

        if (isDirectPathSafe(start, target)) {
            path.add(target);
        } else {
            double safeY = findClearY(start, target);
            
            path.add(new Vec3d(start.x, safeY, start.z));
            path.add(new Vec3d(target.x, safeY, target.z));
            path.add(target);
        }

        return path;
    }

    private static boolean isDirectPathSafe(Vec3d start, Vec3d end) {
        RaycastContext forwardCtx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
        BlockHitResult forwardHit = mc.world.raycast(forwardCtx);

        if (forwardHit.getType() == HitResult.Type.MISS) {
            return true;
        }

        RaycastContext reverseCtx = new RaycastContext(end, start, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
        BlockHitResult reverseHit = mc.world.raycast(reverseCtx);

        if (forwardHit.getType() == HitResult.Type.BLOCK && reverseHit.getType() == HitResult.Type.BLOCK) {
            double dx = forwardHit.getPos().x - reverseHit.getPos().x;
            double dz = forwardHit.getPos().z - reverseHit.getPos().z;
            double horizontalThickness = Math.sqrt(dx * dx + dz * dz);

            return horizontalThickness <= 1.2; 
        }

        return false;
    }

    private static double findClearY(Vec3d start, Vec3d target) {
        double startY = Math.max(start.y, target.y) + 1.0;
        
        double maxSearchY = 320.0; 

        for (double currentY = startY; currentY <= maxSearchY; currentY += 1.0) {
            Vec3d testStart = new Vec3d(start.x, currentY, start.z);
            Vec3d testEnd = new Vec3d(target.x, currentY, target.z);

            if (isDirectPathSafe(testStart, testEnd)) {
                return currentY;
            }
        }
        
        return maxSearchY;
    }

    private static void sendPositionPacket(Vec3d pos, boolean onGround) {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            pos.x, pos.y, pos.z, onGround, false
        ));
    }
}
