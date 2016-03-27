package com.galenframework.rainbow4j.colorscheme;

import java.awt.*;

public class SimpleColorClassifier implements ColorClassifier {
    private final int red;
    private final int blue;
    private final int green;
    private String name;

    public SimpleColorClassifier(String name, Color color) {
        this.name = name;
        this.red = color.getRed();
        this.blue = color.getBlue();
        this.green = color.getGreen();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean holdsColor(int r, int g, int b, int maxColorSquareDistance) {
        int distance = (r - red)*(r - red) + (g - green)*(g - green) + (b - blue)*(b - blue);
        return distance < maxColorSquareDistance;
    }
}
