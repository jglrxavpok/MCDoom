package org.jglrxavpok.mods.mcdoom.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.client.eventhandlers.MCDoomScreenEvents;
import org.jglrxavpok.mods.mcdoom.client.render.RenderPlasmaBall;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.MCDoomProxy;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;
import org.jglrxavpok.mods.mcdoom.common.items.FunWeaponItem;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public class MCDoomClientProxy extends MCDoomProxy {

    private final MCDoomScreenEvents screenEventHandler;

    public MCDoomClientProxy() {
        screenEventHandler = new MCDoomScreenEvents();
    }

    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(screenEventHandler);
        registerItems();

        RenderingRegistry.registerEntityRenderingHandler(PlasmaBallEntity.class, new IRenderFactory<PlasmaBallEntity>() {
            @Override
            public Render<? super PlasmaBallEntity> createRenderFor(RenderManager manager) {
                return new RenderPlasmaBall(manager);
            }
        });
    }

    private void registerItems() {
        Field[] fields = MCDoom.class.getDeclaredFields();
        for(Field f : fields) {
            if(Item.class.isAssignableFrom(f.getType())) {
                try {
                    f.setAccessible(true);
                    Item item = (Item) f.get(MCDoom.instance);
                    String id = item.getUnlocalizedName().substring("item.".length());
                    if(item instanceof FunWeaponItem) {
                        id = id.replace("fun_", "");
                    }
                    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MCDoom.modid + ":" + id, "inventory"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent evt) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent evt) {

    }
}
