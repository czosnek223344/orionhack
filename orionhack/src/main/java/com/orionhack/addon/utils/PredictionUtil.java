package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class PredictionUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Vec3d getPredictedPosition(Entity entity) {
        if (entity == null || mc.world == null) {
            return entity != null ? new Vec3d(entity.getX(), entity.getY(), entity.getZ()) : Vec3d.ZERO;
        }

        if (!(entity instanceof PlayerEntity)) {
            return new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        }

        Vec3d lastPos = new Vec3d(entity.prevX, entity.prevY, entity.prevZ);
        Vec3d currentPos = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        Vec3d velocity = currentPos.subtract(lastPos);

        if (velocity.lengthSquared() < 0.0001) {
            return currentPos;
        }

        double totalSpeed = Math.sqrt(velocity.lengthSquared());

        double ticksAhead = Math.max(1.0, Math.min(10.0, totalSpeed * 0.4));
        Vec3d predicted = currentPos.add(velocity.multiply(ticksAhead));

        double yBonus = totalSpeed >= 2.0 ? Math.min(2.5, totalSpeed * 0.08) : 0.0;
        Vec3d targetPos = new Vec3d(predicted.x, predicted.y + yBonus, predicted.z);

        RaycastContext context = new RaycastContext(
            currentPos,
            targetPos,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            entity
        );

        BlockHitResult hit = mc.world.raycast(context);

        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3d hitPos = hit.getPos();
            Vec3d pullBackDirection = currentPos.subtract(hitPos);

            if (pullBackDirection.lengthSquared() > 0.0001) {
                return hitPos.add(pullBackDirection.normalize().multiply(0.35));
            } else {
                return currentPos;
            }
        }

        return targetPos;
    }
}
