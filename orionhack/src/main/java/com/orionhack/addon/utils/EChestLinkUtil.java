package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;

/**
 * High-quality TP utility for Anarchy / No-AC servers.
 * Inspired by LiveOverflow's bypass techniques.
 * 
 * Features:
 * - 10 blocks per packet stepping.
 * - Bidirectional raycasting to calculate horizontal block thickness.
 * - Allows phasing through 1 block horizontally.
 * - Dynamically routes vertically over thick obstructions (pure vertical phasing).
 */
public class TPUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    // Max blocks per packet (Vanilla No-AC strictly limits to 10.0 blocks per tick/packet)
    private static final double MAX_STEP = 10.0;

    public static void tpTo(Vec3d target) {
        if (mc.player == null || mc.world == null) return;

        Vec3d start = mc.player.getPos();
        
        // Don't waste packets if we are already there
        if (start.distanceTo(target) <= 0.1) return;

        // Calculate the safest, most efficient path
        List<Vec3d> waypoints = calculateSmartPath(start, target);

        // Traverse the generated waypoints
        for (int i = 1; i < waypoints.size(); i++) {
            Vec3d from = waypoints.get(i - 1);
            Vec3d to = waypoints.get(i);
            sendSteps(from, to, i == waypoints.size() - 1); // Only final step might be onGround
        }

        // Apply final position locally to prevent desync
        mc.player.setPosition(target);
    }

    public static void preparePath(Vec3d target) {
        tpTo(target);
    }

    /**
     * Slices a straight line between two waypoints into 10-block packet chunks.
     */
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
            
            // Set onGround to true ONLY on the absolute final packet if the player is landing
            boolean onGround = isFinalWaypoint && (i == steps - 1) && mc.player.isOnGround();
            sendPositionPacket(current, onGround);
        }
    }

    /**
     * Determines if we can go direct or if we need to route vertically to avoid horizontal clipping.
     */
    private static List<Vec3d> calculateSmartPath(Vec3d start, Vec3d target) {
        List<Vec3d> path = new ArrayList<>();
        path.add(start);

        if (isDirectPathSafe(start, target)) {
            // Direct path is clear or has <= 1 block of horizontal clipping
            path.add(target);
        } else {
            // Thick obstruction detected. 
            // Route perfectly vertically to a clear Y-level, move horizontally, then perfectly vertically down.
            double safeY = findClearY(start, target);
            
            path.add(new Vec3d(start.x, safeY, start.z));     // Pure vertical phase UP
            path.add(new Vec3d(target.x, safeY, target.z));   // Horizontal move in clear air
            path.add(target);                                 // Pure vertical phase DOWN
        }

        return path;
    }

    /**
     * Bidirectional raycaster: Fires a ray from A->B and B->A.
     * If an obstruction is found, it calculates the distance between the front-face and back-face hits.
     * Returns true if the path has NO obstructions, OR if the obstruction is <= 1 block thick horizontally.
     */
    private static boolean isDirectPathSafe(Vec3d start, Vec3d end) {
        // Front-to-back raycast
        RaycastContext forwardCtx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
        BlockHitResult forwardHit = mc.world.raycast(forwardCtx);

        if (forwardHit.getType() == HitResult.Type.MISS) {
            return true; // No obstruction at all
        }

        // Back-to-front raycast to find the exit point of the wall
        RaycastContext reverseCtx = new RaycastContext(end, start, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
        BlockHitResult reverseHit = mc.world.raycast(reverseCtx);

        if (forwardHit.getType() == HitResult.Type.BLOCK && reverseHit.getType() == HitResult.Type.BLOCK) {
            // Calculate horizontal thickness (ignore Y difference since vertical phasing is allowed)
            double dx = forwardHit.getPos().x - reverseHit.getPos().x;
            double dz = forwardHit.getPos().z - reverseHit.getPos().z;
            double horizontalThickness = Math.sqrt(dx * dx + dz * dz);

            // 1.2 tolerance allows for diagonal passes through a 1x1 block pillar/wall
            return horizontalThickness <= 1.2; 
        }

        return false;
    }

    /**
     * Scans upwards to find a Y level where horizontal movement to the target X/Z is completely unobstructed.
     */
    private static double findClearY(Vec3d start, Vec3d target) {
        double startY = Math.max(start.y, target.y) + 1.0;
        double maxSearchY = mc.world.getTopY() + 5.0; // Pushes to world roof if underground is solid

        for (double currentY = startY; currentY <= maxSearchY; currentY += 1.0) {
            Vec3d testStart = new Vec3d(start.x, currentY, start.z);
            Vec3d testEnd = new Vec3d(target.x, currentY, target.z);

            if (isDirectPathSafe(testStart, testEnd)) {
                return currentY;
            }
        }
        
        return maxSearchY; // Failsafe: Go strictly above the world ceiling
    }

    private static void sendPositionPacket(Vec3d pos, boolean onGround) {
        // Using your exact 5-arg packet signature, passing our calculated onGround state
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            pos.x, pos.y, pos.z, onGround, false
        ));
    }
}
