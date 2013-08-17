package net.mindengine.galen.utils;

import java.awt.Dimension;

public class GalenUtils {

    public static String formatScreenSize(Dimension screenSize) {
        return String.format("%dx%d", screenSize.width, screenSize.height);
    }

    
}
