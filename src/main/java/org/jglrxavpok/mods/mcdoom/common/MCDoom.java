package org.jglrxavpok.mods.mcdoom.common;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;
import org.jglrxavpok.mods.mcdoom.common.eventhandlers.MCDoomGoreHandler;
import org.jglrxavpok.mods.mcdoom.common.eventhandlers.MCDoomTickEvents;
import org.jglrxavpok.mods.mcdoom.common.eventhandlers.MCDoomWeaponUpdater;
import org.jglrxavpok.mods.mcdoom.common.items.FunWeaponItem;
import org.jglrxavpok.mods.mcdoom.common.items.ItemAmmo;
import org.jglrxavpok.mods.mcdoom.common.items.WeaponItem;
import org.jglrxavpok.mods.mcdoom.common.network.MessageSpawnGoreParticles;
import org.jglrxavpok.mods.mcdoom.common.weapons.EnumWeaponType;
import org.jglrxavpok.mods.mcdoom.common.weapons.WeaponDefinition;

import java.io.InputStreamReader;
import java.util.List;

@Mod(name = "MCDoom", version = "Indev", modid = MCDoom.modid, guiFactory = "org.jglrxavpok.mods.mcdoom.client.GuiFactory")
public class MCDoom {

    public static final String modid = "mcdoom";

    @Mod.Instance(modid)
    public static MCDoom instance;

    @SidedProxy(clientSide = "org.jglrxavpok.mods.mcdoom.client.MCDoomClientProxy", serverSide = "org.jglrxavpok.mods.mcdoom.server.MCDoomServerProxy")
    public static MCDoomProxy proxy;
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(modid);
    private ConfigCategory categoryGraphical;
    private Gson gson;
    public SoundEvent chainsawUp;
    public SoundEvent chainsawIdle;
    public SoundEvent chainsawHit;

    public SoundEvent chainsawFull;
    // only used at pre init to keep in mind which ammo types have to be initialized
    private List<String> ammoToDefine = Lists.newLinkedList();
    private Configuration config;
    private Property doomUIProperty;
    private Property goreProperty;
    private Property maxGoreParticles;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        config = new Configuration(evt.getSuggestedConfigurationFile());
        config.load();
        categoryGraphical = config.getCategory("Graphical");
        doomUIProperty = config.get("Graphical", "Enable DOOM-like UI", true, "Enables the DOOM-like UI ingame");
        goreProperty = config.get("Graphical", "Gore multiplier", 1.0, "Modifies the amount of gore particles spawned ingame");
        maxGoreParticles = config.get("Graphical", "Maximum gore particle count", 10000, "Modifies the amount of particles rendered at the same time");
        goreProperty.setMaxValue(20.);
        goreProperty.setMinValue(0.);
        maxGoreParticles.setMinValue(0);
        maxGoreParticles.setMaxValue(100000);

        config.save();

        gson = new Gson();
        registerItems();
        EntityRegistry.registerModEntity(PlasmaBallEntity.class, "plasma_ball", 0, this, 64, 20, true);

        loadSounds();
        proxy.preInit(evt);

        MinecraftForge.EVENT_BUS.register(new MCDoomWeaponUpdater());
        MinecraftForge.EVENT_BUS.register(new MCDoomGoreHandler());
        MinecraftForge.EVENT_BUS.register(new MCDoomTickEvents());
        registerPackets();
    }

    private void registerPackets() {
        network.registerMessage(MessageSpawnGoreParticles.Handler.class, MessageSpawnGoreParticles.class, 0, Side.CLIENT);
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

            defineAmmo(definition.getAmmoType());
        }

        for(String ammoType : ammoToDefine) {
            ItemAmmo ammo = new ItemAmmo(ammoType);
            ammo.setRegistryName(modid, "ammo_"+ammoType);
            GameRegistry.register(ammo);
        }
    }

    private void defineAmmo(String ammoType) {
        if(!ammoToDefine.contains(ammoType)) {
            ammoToDefine.add(ammoType);
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

        if(definition.getWeaponType() == EnumWeaponType.PROJECTILE) {
            definition.setProjectileType(object.get("projectileType").getAsString());
        }
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

    public Property getDoomUIProperty() {
        return doomUIProperty;
    }

    public Configuration getConfig() {
        return config;
    }

    public Property getGoreProperty() {
        return goreProperty;
    }

    public ConfigCategory getCategoryGraphical() {
        return categoryGraphical;
    }

    public Property getMaxGoreParticles() {
        return maxGoreParticles;
    }
}
