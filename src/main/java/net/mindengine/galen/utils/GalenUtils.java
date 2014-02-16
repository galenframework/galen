/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package net.mindengine.galen.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class GalenUtils {

    private static final String URL_REGEX = "[a-zA-Z0-9]+://.*";
    
    
    public static boolean isUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.matches(URL_REGEX) || url.equals("-");
    }
    
    public static String formatScreenSize(Dimension screenSize) {
        if (screenSize != null) {
            return String.format("%dx%d", screenSize.width, screenSize.height);
        }
        else return "0x0";
    }

    public static Dimension readSize(String sizeText) {
        if (sizeText == null) {
            return null;
        }
        if (!sizeText.matches("[0-9]+x[0-9]+")) {
            throw new RuntimeException("Incorrect screen size: " + sizeText);
        }
        else {
            String[] arr = sizeText.split("x");
            return new Dimension(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        }
    }

    public static File findFile(String specFile) {
        URL resource = GalenUtils.class.getResource(specFile);
        if (resource != null) {
            return new File(resource.getFile());
        }
        else return new File(specFile);
    }
    
    
    public static String makeFullScreenshot(WebDriver driver) throws IOException, InterruptedException {
        byte[] bytes = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        int capturedWidth = image.getWidth();
        int capturedHeight = image.getHeight();
        
        long longScrollHeight = (Long)((JavascriptExecutor)driver).executeScript("return Math.max(" + 
                "document.body.scrollHeight, document.documentElement.scrollHeight," +
                "document.body.offsetHeight, document.documentElement.offsetHeight," +
                "document.body.clientHeight, document.documentElement.clientHeight);"
            );
        
        int scrollHeight = (int)longScrollHeight;
        
        File file = File.createTempFile("screenshot", ".png");
        
        if (Math.abs(capturedHeight - scrollHeight) > 40) {
            int scrollOffset = capturedHeight;
            
            int times = scrollHeight / capturedHeight;
            int leftover = scrollHeight % capturedHeight;
            
            final BufferedImage tiledImage = new BufferedImage(capturedWidth, scrollHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2dTile = tiledImage.createGraphics();
            g2dTile.drawImage(image, 0,0, null);

            
            int scroll = 0;
            for (int i = 0; i < times - 1; i++) {
                scroll += scrollOffset;
                scrollVerticallyTo(driver, scroll);
                Thread.sleep(100);
                BufferedImage nextImage = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)));
                g2dTile.drawImage(nextImage, 0, (i+1) * capturedHeight, null);
            }
            if (leftover > 0) {
                scroll += scrollOffset;
                scrollVerticallyTo(driver, scroll);
                BufferedImage nextImage = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)));
                BufferedImage lastPart = nextImage.getSubimage(0, nextImage.getHeight() - leftover, nextImage.getWidth(), leftover);
                g2dTile.drawImage(lastPart, 0, scrollHeight - leftover, null);
            }
            
            scrollVerticallyTo(driver, 0);
            
            ImageIO.write(tiledImage, "png", file);
        }
        else {
            ImageIO.write(image, "png", file);
        }
        return file.getAbsolutePath();
    }

    public static void scrollVerticallyTo(WebDriver driver, int scroll) {
        ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scroll + ");");
    }
    
}
