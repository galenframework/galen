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
package net.mindengine.galen.browser;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.selenium.SeleniumPage;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class SeleniumBrowser implements Browser {

    private WebDriver driver;

    public SeleniumBrowser(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public void changeWindowSize(Dimension windowSize) {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(windowSize.width, windowSize.height));
    }

    @Override
    public void load(String url) {
        driver.get(url);
    }

    @Override
    public void executeJavascript(String javascript) {
        ((JavascriptExecutor)driver).executeScript(javascript);
    }

    @Override
    public Page getPage() {
        return new SeleniumPage(driver);
    }

    @Override
    public String getUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public Dimension getScreenSize() {
        org.openqa.selenium.Dimension windowSize = driver.manage().window().getSize();
        return new Dimension(windowSize.getWidth(), windowSize.getHeight());
    }

    @Override
    public String createScreenshot() {
        if (GalenConfig.getConfig().getBooleanProperty("galen.browser.screenshots.fullPage", true)) {
            try {
                return makeFullScreenshots();
            } catch (Exception e) {
                throw new RuntimeException("Error making screenshot", e);
            }
        }
        else return makeSimpleScreenshot();
    }
    
    private String makeFullScreenshots() throws IOException, InterruptedException {
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

            for (int i = 0; i < times - 1; i++) {
                ((JavascriptExecutor)driver).executeAsyncScript("document.body.scrollTop += " + scrollOffset + ";arguments[arguments.length - 1]();");
                Thread.sleep(100);
                BufferedImage nextImage = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)));
                g2dTile.drawImage(nextImage, 0, (i+1) * capturedHeight, null);
            }
            if (leftover > 0) {
                ((JavascriptExecutor)driver).executeAsyncScript("document.body.scrollTop += " + scrollOffset + ";arguments[arguments.length - 1]();");
                BufferedImage nextImage = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)));
                BufferedImage lastPart = nextImage.getSubimage(0, nextImage.getHeight() - leftover, nextImage.getWidth(), leftover);
                g2dTile.drawImage(lastPart, 0, scrollHeight - leftover, null);
            }
            
            ImageIO.write(tiledImage, "png", file);
        }
        else {
            ImageIO.write(image, "png", file);
        }
        return file.getAbsolutePath();
    }

    private String makeSimpleScreenshot() {
        File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        return file.getAbsolutePath();
    }
    
    @Override
    public void refresh() {
        driver.navigate().refresh();
    }

}
