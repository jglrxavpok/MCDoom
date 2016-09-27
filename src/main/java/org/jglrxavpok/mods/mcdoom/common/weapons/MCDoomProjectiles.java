package org.jglrxavpok.mods.mcdoom.common.weapons;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.jglrxavpok.mods.mcdoom.common.entity.MCDoomProjectileEntity;

import java.util.Map;

public class MCDoomProjectiles {

    private static final Map<String, ProjectileSupplier> projectiles;

    static {
        projectiles = Maps.newHashMap();
    }

    public static MCDoomProjectileEntity create(World world, String type, EntityLivingBase shooter) {
        return projectiles.get(type).create(world, shooter);
    }

    public static void registerProjectile(String type, ProjectileSupplier supplier) {
        projectiles.put(type, supplier);
    }
}
