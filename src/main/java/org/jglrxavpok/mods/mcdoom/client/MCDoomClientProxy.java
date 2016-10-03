package org.jglrxavpok.mods.mcdoom.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import fr.minecraftforgefrance.sfd.common.SFDProxy;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.client.eventhandlers.MCDoomScreenEvents;
import org.jglrxavpok.mods.mcdoom.client.eventhandlers.MCDoomSoundEvents;
import org.jglrxavpok.mods.mcdoom.client.particle.EntityGoreFX;
import org.jglrxavpok.mods.mcdoom.client.render.RenderPlasmaBall;
import org.jglrxavpok.mods.mcdoom.client.render.WeaponRenderer;
import org.jglrxavpok.mods.mcdoom.client.render.WeaponRendererLoader;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class MCDoomClientProxy extends SFDProxy {

    private final MCDoomScreenEvents screenEventHandler;
    private final WeaponRendererLoader weaponRendererLoader;
    private final MCDoomSoundEvents soundEventsHandler;
    private final Map<String, WeaponRenderer> renderers;
    private final LinkedList<EntityGoreFX> particlesToAdd;
    private final LinkedList<EntityGoreFX> particlesToRemove;
    private final ArrayDeque<EntityGoreFX> goreParticles;

    public MCDoomClientProxy() {
        renderers = Maps.newHashMap();
        screenEventHandler = new MCDoomScreenEvents(this);
        soundEventsHandler = new MCDoomSoundEvents();
        weaponRendererLoader = new WeaponRendererLoader();
        goreParticles = new ArrayDeque<EntityGoreFX>();
        particlesToAdd = Lists.newLinkedList();
        particlesToRemove = Lists.newLinkedList();
    }

    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        MCDoom.instance.getGoreProperty().setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        MCDoom.instance.getMaxGoreParticles().setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

        MinecraftForge.EVENT_BUS.register(screenEventHandler);
        MinecraftForge.EVENT_BUS.register(soundEventsHandler);
        registerItems();

        RenderingRegistry.registerEntityRenderingHandler(PlasmaBallEntity.class, new IRenderFactory<PlasmaBallEntity>() {
            @Override
            public Render<? super PlasmaBallEntity> createRenderFor(RenderManager manager) {
                return new RenderPlasmaBall(manager);
            }
        });

        loadWeaponRenderers();
    }

    private void loadWeaponRenderers() {
        String[] ids = MCDoom.instance.getWeaponIDs();
        for(String id : ids) {
            JsonObject renderObject = MCDoom.instance.readWeaponFile(id).getAsJsonObject("render");
            WeaponRenderer renderer = weaponRendererLoader.loadFromJson(id, renderObject);
            renderers.put(id, renderer);
        }
    }

    private void registerItems() {
        for(String id : MCDoom.instance.getWeaponIDs()) {
            Item item = Item.getByNameOrId(MCDoom.modid+":"+id);
            Item funVersion = Item.getByNameOrId(MCDoom.modid+":fun_"+id);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MCDoom.modid + ":" + id, "inventory"));
            ModelLoader.setCustomModelResourceLocation(funVersion, 0, new ModelResourceLocation(MCDoom.modid + ":"+id, "inventory"));
        }
    }

    @Override
    public void init(FMLInitializationEvent evt) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent evt) {

    }

    @Override
    public void spawnParticle(Particle particle) {
        particlesToAdd.add((EntityGoreFX) particle);
    }

    @Override
    public void onTickEvent(TickEvent evt) {
        if(evt instanceof TickEvent.ClientTickEvent && evt.phase == TickEvent.Phase.END) {
            for (EntityGoreFX p : goreParticles) {
                p.onUpdate();
                if(!p.isAlive())
                    particlesToRemove.add(p);
            }

            synchronized (goreParticles) {
                goreParticles.addAll(particlesToAdd);
                goreParticles.removeAll(particlesToRemove);
            }

            particlesToRemove.clear();
            particlesToAdd.clear();

            final int maxParticles = MCDoom.instance.getMaxGoreParticles().getInt();
            int overflow = goreParticles.size() - maxParticles;
            for (int i = 0; i < overflow; i++) {
                goreParticles.removeFirst();
            }
        }
    }

    public WeaponRenderer getRenderer(String id) {
        return renderers.get(id);
    }

    public ArrayDeque<EntityGoreFX> getGoreParticles() {
        return goreParticles;
    }
}
