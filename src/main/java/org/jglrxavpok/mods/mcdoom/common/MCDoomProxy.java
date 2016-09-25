package org.jglrxavpok.mods.mcdoom.common;

import net.minecraft.client.particle.Particle;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public abstract class MCDoomProxy {

    public abstract void preInit(FMLPreInitializationEvent evt);

    public abstract void init(FMLInitializationEvent evt);

    public abstract void postInit(FMLPostInitializationEvent evt);

    public abstract void spawnParticle(Particle particle);

    public abstract void onTickEvent(TickEvent evt);
}
