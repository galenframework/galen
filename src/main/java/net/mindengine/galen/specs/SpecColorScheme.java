package net.mindengine.galen.specs;

import java.util.List;

import net.mindengine.galen.specs.colors.ColorRange;

public class SpecColorScheme extends Spec {

    private List<ColorRange> colorRanges;

    public List<ColorRange> getColorRanges() {
        return this.colorRanges;
    }

    public void setColorRanges(List<ColorRange> colorRanges) {
        this.colorRanges = colorRanges;
    }

}
