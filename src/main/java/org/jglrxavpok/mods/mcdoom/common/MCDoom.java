package org.jglrxavpok.mods.mcdoom.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jglrxavpok.mods.mcdoom.client.MCDoomClientProxy;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;
import org.jglrxavpok.mods.mcdoom.common.eventhandlers.MCDoomWeaponUpdater;
import org.jglrxavpok.mods.mcdoom.common.items.BFGItem;

@Mod(name = "MCDoom", version = "0.0.1", modid = MCDoom.modid)
public class MCDoom {

    public static final String modid = "mcdoom";

    @Mod.Instance(modid)
    public static MCDoom instance;

    private BFGItem bfg;

    @SidedProxy(clientSide = "org.jglrxavpok.mods.mcdoom.client.MCDoomClientProxy", serverSide = "org.jglrxavpok.mods.mcdoom.server.MCDoomServerProxy")
    public static MCDoomProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        bfg = new BFGItem();
        bfg.setRegistryName(modid, bfg.getUnlocalizedName());

        GameRegistry.register(bfg);
        EntityRegistry.registerModEntity(PlasmaBallEntity.class, "plasma_ball", 0, this, 64, 20, true);

        proxy.preInit(evt);

        MinecraftForge.EVENT_BUS.register(new MCDoomWeaponUpdater());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        proxy.init(evt);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        proxy.postInit(evt);
    }

    public BFGItem getBFGItem() {
        return bfg;
    }
}
