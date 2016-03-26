package com.galenframework.rainbow4j.colorscheme;

import java.util.Map;

public class CustomSpectrum {
    private final Integer totalPixels;
    private final Map<String, Integer> collectedColors;
    private final Integer otherColors;

    public CustomSpectrum(Map<String, Integer> collectedColors, Integer otherColors, Integer totalPixels) {
        this.collectedColors = collectedColors;
        this.otherColors = otherColors;
        this.totalPixels = totalPixels;
    }

    public Integer getTotalPixels() {
        return totalPixels;
    }

    public Integer getOtherColors() {
        return otherColors;
    }

    public Map<String, Integer> getCollectedColors() {
        return collectedColors;
    }
}
