package net.mindengine.galen.specs.colors;

import java.awt.Color;

import net.mindengine.galen.specs.Range;

public class ColorRange {

    private Range range;
    private Color color;

    public ColorRange(Color color, Range range) {
        this.color = color;
        this.range = range;
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
