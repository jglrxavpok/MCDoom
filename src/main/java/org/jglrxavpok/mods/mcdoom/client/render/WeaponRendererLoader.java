package org.jglrxavpok.mods.mcdoom.client.render;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;

import java.util.Map;

public class WeaponRendererLoader {

    public WeaponRenderer loadFromJson(String weaponID, JsonObject renderObject) {
        // Load texture infos
        Map<String, String> textureFiles = Maps.newHashMap();
        Map<String, Integer> textureWidths = Maps.newHashMap();
        Map<String, Integer> textureHeights = Maps.newHashMap();
        JsonArray textureList = renderObject.getAsJsonArray("textures");
        for (int i = 0; i < textureList.size(); i++) {
            JsonObject textureObject = textureList.get(i).getAsJsonObject();
            String id = textureObject.get("id").getAsString();
            String file = textureObject.get("file").getAsString();
            int width = textureObject.get("width").getAsInt();
            int height = textureObject.get("height").getAsInt();

            textureFiles.put(id, file);
            textureWidths.put(id, width);
            textureHeights.put(id, height);
        }

        // Region infos
        Map<String, String> regionTextures = Maps.newHashMap();
        Map<String, TextureRegion> textureRegions = Maps.newHashMap();
        JsonArray regionList = renderObject.getAsJsonArray("regions");
        for (int i = 0; i < regionList.size(); i++) {
            JsonObject regionObject = regionList.get(i).getAsJsonObject();
            String id = regionObject.get("id").getAsString();
            String texture = regionObject.get("texture").getAsString();
            JsonArray regionComponents = regionObject.get("region").getAsJsonArray();
            int minU = regionComponents.get(0).getAsInt();
            int minV = regionComponents.get(1).getAsInt();
            int maxU = regionComponents.get(2).getAsInt();
            int maxV = regionComponents.get(3).getAsInt();

            if(!textureFiles.containsKey(texture)) {
                throw new IllegalArgumentException("Region with id "+id+" tried to load non-existing texture with id "+id);
            }
            float textureWidth = textureWidths.get(texture);
            float textureHeight = textureHeights.get(texture);

            TextureRegion region = new TextureRegion((float)minU / textureWidth, (float)minV / textureHeight, (float)maxU / textureWidth, (float)maxV / textureHeight);
            regionTextures.put(id, texture);
            textureRegions.put(id, region);
        }

        // Layer infos
        LayeredWeaponRenderer renderer = new LayeredWeaponRenderer();
        JsonArray layerList = renderObject.getAsJsonArray("layers");
        for (int i = 0; i < layerList.size(); i++) {
            JsonObject layerObject = layerList.get(i).getAsJsonObject();
            String condition = layerObject.get("condition").getAsString();
            String regionID = layerObject.get("region").getAsString();
            TextureRegion region = textureRegions.get(regionID);
            boolean bobbing = true;
            if(layerObject.has("bobbing"))
                bobbing = layerObject.get("bobbing").getAsBoolean();
            int offsetX = 0;
            int offsetY = 0;
            if(layerObject.has("offset")) {
                JsonArray offset = layerObject.getAsJsonArray("offset");
                offsetX = offset.get(0).getAsInt();
                offsetY = offset.get(1).getAsInt();
            }
            int zLevelOffset = 0;
            if(layerObject.has("zPriority")) {
                zLevelOffset = layerObject.get("zPriority").getAsInt();
            }
            String texture = regionTextures.get(regionID);
            float textureWidth = textureWidths.get(texture);
            float textureHeight = textureHeights.get(texture);
            ResourceLocation textureLocation = new ResourceLocation(MCDoom.modid, "textures/"+textureFiles.get(regionTextures.get(regionID)));
            WeaponPredicate predicate = WeaponPredicate.createFromString(condition);
            renderer.addLayer(predicate, new LayeredWeaponRenderer.WeaponLayer(bobbing, textureLocation, region, offsetX, offsetY, zLevelOffset, textureWidth, textureHeight));
        }



        return renderer;
    }
}
