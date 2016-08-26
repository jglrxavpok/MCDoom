package org.jglrxavpok.mods.mcdoom.common.utils;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MathUtils {

    public static RayTraceResult raycast(World world, final Vec3d from, Vec3d direction, float maxDistance, final Predicate<Entity> entityFilter) {
        direction = direction.normalize();
        final float maxDistanceSq = maxDistance * maxDistance;
        List<Entity> entities = world.getEntities(Entity.class, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return input != null && entityFilter.apply(input) && input.getDistanceSq(from.xCoord, from.yCoord, from.zCoord) < maxDistanceSq;
            }
        });
        Entity closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Entity e : entities) { // now check that the entity is in front of the facing direction
            Vec3d fromStartToEntityVec = new Vec3d(e.getPosition().add(e.width/2f, e.height/2f, e.width/2f)).subtract(from);
            double dotResult = direction.dotProduct(fromStartToEntityVec);

            // could have normalized the vectors here but this would create even more Vec3d instances that needed
            double angle = Math.acos(dotResult / (fromStartToEntityVec.lengthVector() * direction.lengthVector()));
            if(angle <= Math.PI/4f && shortestDistance > fromStartToEntityVec.lengthVector()) {
                closest = e;
                shortestDistance = fromStartToEntityVec.lengthVector();
            }
        }
        if(closest == null) {
            Vec3d dir = direction.normalize().scale(maxDistance);
            Vec3d end = from.add(dir);
            return world.rayTraceBlocks(from, end, false, true, true);
        } else {
            return new RayTraceResult(closest);
        }
    }
}
