package net.qilla.selectionplugin.tools.regionselection;

import net.qilla.selectionplugin.util.Countable;

import java.util.Arrays;
import java.util.List;

public final class WandSettings {

    private WandVariant variant = WandVariant.WHITE;
    private Countable wandReach = new Countable(5,1, 128);

    WandSettings() {
    }

    public void setVariant(WandVariant wandVariant) {
        this.variant = wandVariant;
    }

    public void nextVariant() {
        List<WandVariant> wandVariants = Arrays.stream(WandVariant.values()).toList();
        int index = wandVariants.indexOf(this.variant);
        index = (index >= wandVariants.size() - 1) ? 0 : index + 1;
        this.variant = wandVariants.get(index);
    }

    public void previousVariant() {
        List<WandVariant> wandVariants = Arrays.stream(WandVariant.values()).toList();
        int index = wandVariants.indexOf(this.variant);
        index = (index <= 0) ? wandVariants.size() - 1 : index - 1;
        this.variant = wandVariants.get(index);
    }

    public WandVariant getVariant() {
        return variant;
    }

    public Countable wandReach() {
        return wandReach;
    }
}
