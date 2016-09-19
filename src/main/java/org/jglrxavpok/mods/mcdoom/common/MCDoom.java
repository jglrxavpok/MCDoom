package org.jglrxavpok.mods.mcdoom.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.Sound;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;
import org.jglrxavpok.mods.mcdoom.common.eventhandlers.MCDoomWeaponUpdater;
import org.jglrxavpok.mods.mcdoom.common.items.FunWeaponItem;
import org.jglrxavpok.mods.mcdoom.common.items.WeaponItem;
import org.jglrxavpok.mods.mcdoom.common.weapons.EnumWeaponType;
import org.jglrxavpok.mods.mcdoom.common.weapons.WeaponDefinition;

import java.io.InputStreamReader;
import java.lang.reflect.Field;

@Mod(name = "MCDoom", version = "0.0.1", modid = MCDoom.modid)
public class MCDoom {

    public static final String modid = "mcdoom";

    @Mod.Instance(modid)
    public static MCDoom instance;

    @SidedProxy(clientSide = "org.jglrxavpok.mods.mcdoom.client.MCDoomClientProxy", serverSide = "org.jglrxavpok.mods.mcdoom.server.MCDoomServerProxy")
    public static MCDoomProxy proxy;
    private Gson gson;
    public SoundEvent chainsawUp;
    public SoundEvent chainsawIdle;
    public SoundEvent chainsawHit;
    public SoundEvent chainsawFull;
    private boolean doomUIEnabled;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        gson = new Gson();
        registerItems();
        EntityRegistry.registerModEntity(PlasmaBallEntity.class, "plasma_ball", 0, this, 64, 20, true);

        loadSounds();
        proxy.preInit(evt);

        MinecraftForge.EVENT_BUS.register(new MCDoomWeaponUpdater());
    }

    public void loadSounds() {
        chainsawUp = registerSound(new ResourceLocation(MCDoom.modid, "chainsaw.up"));
        chainsawIdle = registerSound(new ResourceLocation(MCDoom.modid, "chainsaw.idle"));
        chainsawFull = registerSound(new ResourceLocation(MCDoom.modid, "chainsaw.full"));
        chainsawHit = registerSound(new ResourceLocation(MCDoom.modid, "chainsaw.hit"));
    }

    private SoundEvent registerSound(ResourceLocation location) {
        return GameRegistry.register(new SoundEvent(location).setRegistryName(location));
    }

    private void registerItems() {
        for(String id : getWeaponIDs()) {
            WeaponDefinition definition = loadWeapon(id);
            Item weapon = new WeaponItem(definition);
            Item funVersion = new FunWeaponItem(definition);

            weapon.setRegistryName(modid, definition.getId());
            GameRegistry.register(weapon);

            funVersion.setRegistryName(modid, "fun_"+definition.getId());
            GameRegistry.register(funVersion);
        }
    }

    public String[] getWeaponIDs() {
        return new String[]{ "bfg9000", "chainsaw" };
    }

    private WeaponDefinition loadWeapon(String id) {
        JsonObject object = readWeaponFile(id);
        WeaponDefinition definition = new WeaponDefinition();
        definition.setId(object.get("id").getAsString());
        definition.setCooldown(object.get("cooldown").getAsInt());
        definition.setPreFiringPause(object.get("triggerDelay").getAsInt());
        definition.setAmmoType(object.get("ammoType").getAsString());
        definition.setBaseDamage(object.get("baseDamage").getAsInt());
        definition.setWeaponType(EnumWeaponType.valueOf(object.get("weaponType").getAsString().toUpperCase()));
        return definition;
    }

    public JsonObject readWeaponFile(String weaponID) {
        String definitionFileLocation = "/assets/"+modid+"/weapons/"+weaponID+".json";
        return gson.fromJson(new InputStreamReader(getClass().getResourceAsStream(definitionFileLocation)), JsonObject.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        proxy.init(evt);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        proxy.postInit(evt);
    }

    public boolean isDoomUIEnabled() {
        // TODO: return doomUIEnabled;
        return true;
    }
}
