package net.qilla.selectionplugin.regionselection.settings;

import net.qilla.selectionplugin.regionselection.wand.WandVariant;

public class PlayerWandSettings {

    private WandVariant wandVariant;
    private boolean changingVariant;

    public PlayerWandSettings() {
        this.wandVariant = WandVariant.ORANGE;
    }

    public void setVariant(WandVariant wandVariant) {
        this.wandVariant = wandVariant;
    }

    public void toggleVariant() {
        this.changingVariant = !this.changingVariant;
    }

    public WandVariant getVariant() {
        return wandVariant;
    }

    public boolean isChangingVariant() {
        return changingVariant;
    }
}
