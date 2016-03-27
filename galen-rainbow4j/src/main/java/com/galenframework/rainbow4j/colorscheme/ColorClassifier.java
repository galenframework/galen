package com.galenframework.rainbow4j.colorscheme;

public interface ColorClassifier {

    String getName();
    boolean holdsColor(int r, int g, int b, int maxColorSquareDistance);
}
