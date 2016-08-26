package org.jglrxavpok.mods.mcdoom.common.weapons;

public class WeaponDefinition {

    private int cooldown;
    private int preFiringPause;
    private String id;
    private String ammoType;

    public WeaponDefinition() {

    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getPreFiringPause() {
        return preFiringPause;
    }

    public void setPreFiringPause(int preFiringPause) {
        this.preFiringPause = preFiringPause;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAmmoType(String ammoType) {
        this.ammoType = ammoType;
    }

    public String getAmmoType() {
        return ammoType;
    }
}
