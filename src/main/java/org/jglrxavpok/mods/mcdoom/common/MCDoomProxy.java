package org.jglrxavpok.mods.mcdoom.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class MCDoomProxy {

    public abstract void preInit(FMLPreInitializationEvent evt);

    public abstract void init(FMLInitializationEvent evt);

    public abstract void postInit(FMLPostInitializationEvent evt);
}
