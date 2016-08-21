package org.jglrxavpok.mods.mcdoom.client.render;

public class TextureRegion {
    private final float minU;
    private final float minV;
    private final float maxU;
    private final float maxV;

    public TextureRegion(float minU, float minV, float maxU, float maxV) {
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
    }

    public float getMinU() {
        return minU;
    }

    public float getMinV() {
        return minV;
    }

    public float getMaxU() {
        return maxU;
    }

    public float getMaxV() {
        return maxV;
    }
}
