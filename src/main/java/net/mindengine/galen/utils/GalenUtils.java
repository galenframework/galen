package net.mindengine.galen.utils;

import java.awt.Dimension;

public class GalenUtils {

    private static final String URL_REGEX = "[a-zA-Z0-9]+://.*";
    
    
    public static boolean isUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.matches(URL_REGEX);
    }
    
    public static String formatScreenSize(Dimension screenSize) {
        return String.format("%dx%d", screenSize.width, screenSize.height);
    }

    public static Dimension readSize(String sizeText) {
        if (!sizeText.matches("[0-9]+x[0-9]+")) {
            throw new RuntimeException("Incorrect screen size: " + sizeText);
        }
        else {
            String[] arr = sizeText.split("x");
            return new Dimension(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        }
    }

    
}
