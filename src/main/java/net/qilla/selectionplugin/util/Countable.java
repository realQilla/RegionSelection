package net.qilla.selectionplugin.util;

public final class Countable {

    private int value;
    private final int min;
    private final int max;

    public Countable(int value, int min, int max) {
        this.value = Math.max(min, Math.min(value, max));
        this.min = min;
        this.max = max;
    }

    public int setValue(int value) {
        return this.value = Math.max(min, Math.min(value, max));
    }

    public int increment(int value) {
        return setValue(this.value + value);
    }

    public int decrement(int value) {
        return setValue(this.value - value);
    }

    public int getValue() {
        return value;
    }
}
