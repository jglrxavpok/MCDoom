package org.jglrxavpok.mods.mcdoom.common.utils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MathUtils {

    public static RayTraceResult raycast(World world, final Vec3d from, Vec3d direction, float maxDistance, final Predicate<Entity> entityFilter) {
        direction = direction.normalize();
        Entity closest = null;

        Vec3d end = from.addVector(direction.xCoord * maxDistance, direction.yCoord * maxDistance, direction.zCoord * maxDistance);
        AxisAlignedBB aabb = new AxisAlignedBB(from.xCoord, from.yCoord, from.zCoord, from.xCoord, from.yCoord, from.zCoord).addCoord(direction.xCoord * maxDistance, direction.yCoord * maxDistance, direction.zCoord * maxDistance).expand(1.0D, 1.0D, 1.0D);
        List<Entity> list = world.getEntitiesInAABBexcluding(null, aabb, Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity input)
            {
                return input != null && input.canBeCollidedWith();
            }
        }, entityFilter));
        double shortestDistance = maxDistance;

        for (Entity entity : list)
        {
            AxisAlignedBB entityAABB = entity.getEntityBoundingBox();
            RayTraceResult intercept = entityAABB.calculateIntercept(from, end);

            if (entityAABB.isVecInside(from))
            {
                if (shortestDistance >= 0.0D)
                {
                    closest = entity;
                    shortestDistance = 0.0D;
                }
            }
            else if (intercept != null)
            {
                double distance = from.distanceTo(intercept.hitVec);

                if (distance < shortestDistance || shortestDistance == 0.0D)
                {
                    closest = entity;
                    shortestDistance = distance;
                }
            }
        }

        if(closest == null) {
            return world.rayTraceBlocks(from, end, false, true, true);
        } else {
            return new RayTraceResult(closest);
        }
    }
}
