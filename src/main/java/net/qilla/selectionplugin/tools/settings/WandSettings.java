package net.qilla.selectionplugin.tools.settings;

import net.qilla.selectionplugin.tools.regionselection.WandVariant;

import java.util.Arrays;
import java.util.List;

public class WandSettings {

    private WandVariant wandVariant;

    public WandSettings() {
        this.wandVariant = WandVariant.ORANGE;
    }

    public void setVariant(WandVariant wandVariant) {
        this.wandVariant = wandVariant;
    }

    public void nextVariant() {
        List<WandVariant> wandVariants = Arrays.stream(WandVariant.values()).toList();
        int index = wandVariants.indexOf(this.wandVariant);
        index = (index >= wandVariants.size() - 1) ? 0 : index + 1;
        this.wandVariant = wandVariants.get(index);
    }

    public void previousVariant() {
        List<WandVariant> wandVariants = Arrays.stream(WandVariant.values()).toList();
        int index = wandVariants.indexOf(this.wandVariant);
        index = (index <= 0) ? wandVariants.size() - 1 : index - 1;
        this.wandVariant = wandVariants.get(index);
    }

    public WandVariant getVariant() {
        return wandVariant;
    }
}
