/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.mindengine.galen.api.UnregisteredTestSession;
import net.mindengine.galen.browser.SeleniumBrowser;
import net.mindengine.galen.browser.SeleniumBrowserFactory;
import net.mindengine.galen.browser.SeleniumGridBrowserFactory;
import net.mindengine.galen.config.GalenConfig;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.runner.CompleteListener;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.tests.GalenProperties;
import net.mindengine.galen.tests.TestSession;
import net.mindengine.rainbow4j.Rainbow4J;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalenUtils {

    private final static Logger LOG = LoggerFactory.getLogger(GalenUtils.class);
    
    private static final String URL_REGEX = "[a-zA-Z0-9]+://.*";
    public static final String JS_RETRIEVE_DEVICE_PIXEL_RATIO = "var pr = window.devicePixelRatio; if (pr != undefined && pr != null)return pr; else return 1.0;";


    public static boolean isUrl(final String url) {
        if (url == null) {
            return false;
        }
        return url.matches(URL_REGEX) || url.equals("-");
    }
    
    public static String formatScreenSize(final Dimension screenSize) {
        if (screenSize != null) {
            return String.format("%dx%d", screenSize.width, screenSize.height);
        } else {
            return "0x0";
        }
    }

    public static Dimension readSize(final String sizeText) {
        if (sizeText == null) {
            return null;
        }
        if (!sizeText.matches("[0-9]+x[0-9]+")) {
            throw new RuntimeException("Incorrect screen size: " + sizeText);
        }
        else {
            final String[] arr = sizeText.split("x");
            return new Dimension(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        }
    }

    public static File findFile(final String specFile) {
        final URL resource = GalenUtils.class.getResource(specFile);
        if (resource != null) {
            return new File(resource.getFile());
        } else {
            return new File(specFile);
        }
    }
    
    
    public static File makeFullScreenshot(final WebDriver driver) throws IOException, InterruptedException {
        final byte[] bytes = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        final BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        final int capturedWidth = image.getWidth();
        final int capturedHeight = image.getHeight();

        // TODO Chop of the header and footer for Safari on iOS.

        final long longScrollHeight = (Long)((JavascriptExecutor)driver).executeScript("return Math.max(" + 
                "document.body.scrollHeight, document.documentElement.scrollHeight," +
                "document.body.offsetHeight, document.documentElement.offsetHeight," +
                "document.body.clientHeight, document.documentElement.clientHeight);"
            );

        final Double devicePixelRatio = ((Number)((JavascriptExecutor)driver).executeScript(JS_RETRIEVE_DEVICE_PIXEL_RATIO)).doubleValue();

        final int scrollHeight = (int)longScrollHeight;

        final File file = File.createTempFile("screenshot", ".png");

        final int adaptedCapturedHeight = (int)((capturedHeight) / devicePixelRatio);

        BufferedImage resultingImage;

        if (Math.abs(adaptedCapturedHeight - scrollHeight) > 40) {
            final int scrollOffset = adaptedCapturedHeight;
            
            final int times = scrollHeight / adaptedCapturedHeight;
            final int leftover = scrollHeight % adaptedCapturedHeight;

            final BufferedImage tiledImage = new BufferedImage(capturedWidth, (int)((scrollHeight) * devicePixelRatio), BufferedImage.TYPE_INT_RGB);
            final Graphics2D g2dTile = tiledImage.createGraphics();
            g2dTile.drawImage(image, 0,0, null);

            
            int scroll = 0;
            for (int i = 0; i < times - 1; i++) {
                scroll += scrollOffset;
                scrollVerticallyTo(driver, scroll);
                final BufferedImage nextImage = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)));
                g2dTile.drawImage(nextImage, 0, (i+1) * capturedHeight, null);
            }
            if (leftover > 0) {
                scroll += scrollOffset;
                scrollVerticallyTo(driver, scroll);
                final BufferedImage nextImage = ImageIO.read(new ByteArrayInputStream(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES)));
                final BufferedImage lastPart = nextImage.getSubimage(0, nextImage.getHeight() - (int)((leftover) * devicePixelRatio), nextImage.getWidth(), leftover);
                g2dTile.drawImage(lastPart, 0, times * capturedHeight, null);
            }
            
            scrollVerticallyTo(driver, 0);

            resultingImage = tiledImage;
        }
        else {
            resultingImage = image;
        }

        if (GalenConfig.getConfig().shouldAutoresizeScreenshots()) {
            resultingImage = GalenUtils.resizeScreenshotIfNeeded(driver, resultingImage);
        }

        ImageIO.write(resultingImage, "png", file);
        return file;
    }


    /**
     * Check the devicePixelRatio and adapts the size of the screenshot as if the ratio was 1.0
     * @param driver
     * @param screenshotImage
     * @return
     */
    public static BufferedImage resizeScreenshotIfNeeded(final WebDriver driver, final BufferedImage screenshotImage) {
        final Double devicePixelRatio = ((Number)((JavascriptExecutor)driver).executeScript(JS_RETRIEVE_DEVICE_PIXEL_RATIO)).doubleValue();

        if (devicePixelRatio > 1.0 && screenshotImage.getWidth() > 0) {
            final Long screenSize = (Long) ((JavascriptExecutor) driver).executeScript("return Math.max(" +
                            "document.body.scrollWidth, document.documentElement.scrollWidth," +
                            "document.body.offsetWidth, document.documentElement.offsetWidth," +
                            "document.body.clientWidth, document.documentElement.clientWidth);"
            );

            final Double estimatedPixelRatio = ((double)screenshotImage.getWidth()) / ((double)screenSize);

            if (estimatedPixelRatio > 1.0) {

                final int newWidth = (int) (screenshotImage.getWidth() / estimatedPixelRatio);
                final int newHeight = (int) (screenshotImage.getHeight() / estimatedPixelRatio);

                final Image tmp = screenshotImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                final BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

                final Graphics2D g2d = scaledImage.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();

                return scaledImage;
            } else {
                return screenshotImage;
            }
        } else {
            return screenshotImage;
        }
    }

    public static void scrollVerticallyTo(final WebDriver driver, final int scroll) {
        ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scroll + ");");
        try {
            waitUntilItIsScrolledToPosition(driver, scroll);
        } catch (final InterruptedException e) {
            LOG.trace("Interrupt error during scrolling occurred.", e);
        }
    }

    private static void waitUntilItIsScrolledToPosition(final WebDriver driver, final int scrollPosition) throws InterruptedException {
        final int hardTime = GalenConfig.getConfig().getIntProperty(GalenConfig.SCREENSHOT_FULLPAGE_SCROLLWAIT, 0);
        if (hardTime > 0) {
            Thread.sleep(hardTime);
        }

        int time = 250;
        boolean isScrolledToPosition = false;
        while(time >= 0 && !isScrolledToPosition) {
            Thread.sleep(50);
            time -= 50;
            isScrolledToPosition = Math.abs(obtainVerticalScrollPosition(driver) - scrollPosition) < 3;
        }
    }

    private static int obtainVerticalScrollPosition(final WebDriver driver) {
        final Long scrollLong = (Long) ((JavascriptExecutor)driver).executeScript("return (window.pageYOffset !== undefined) ? window.pageYOffset : (document.documentElement || document.body.parentNode || document.body).scrollTop;");
        return scrollLong.intValue();
    }

    public static String convertToFileName(final String name) {
        return name.toLowerCase().replaceAll("[^\\dA-Za-z\\.\\-]", " ").replaceAll("\\s+", "-");
    }

    
    /**
     * Needed for Javascript based tests
     * @param browserType
     * @return
     */
    public static WebDriver createDriver(String browserType, final String url, final String size) {
        if (browserType == null) { 
            browserType = GalenConfig.getConfig().getDefaultBrowser();
        }
        
        final SeleniumBrowser browser = (SeleniumBrowser) new SeleniumBrowserFactory(browserType).openBrowser();
        
        if (url != null && !url.trim().isEmpty()) {
            browser.load(url);    
        }
        
        if (size != null && !size.trim().isEmpty()) {
            browser.changeWindowSize(GalenUtils.readSize(size));
        }
        
        return browser.getDriver();
    }
    
    public static WebDriver createGridDriver(final String gridUrl, final String browserName, final String browserVersion, final String platform, final Map<String, String> desiredCapabilities, final String size) {
        final SeleniumGridBrowserFactory factory = new SeleniumGridBrowserFactory(gridUrl);
        factory.setBrowser(browserName);
        factory.setBrowserVersion(browserVersion);
        
        if (platform != null) {
            factory.setPlatform(Platform.valueOf(platform));
        }
        
        if (desiredCapabilities != null) {
            factory.setDesiredCapabilites(desiredCapabilities);
        }
        
        final WebDriver driver = ((SeleniumBrowser)factory.openBrowser()).getDriver();
        
        GalenUtils.resizeDriver(driver, size);
        return driver;
    }
    
    public static void resizeDriver(final WebDriver driver, final String sizeText) {
        if (sizeText != null && !sizeText.trim().isEmpty()) {
            final Dimension size = GalenUtils.readSize(sizeText);
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(size.width, size.height));
        }
    }

    /**
     * Needed for Javascript based tests
     * @param driver
     * @param fileName
     * @param includedTags
     * @param excludedTags
     * @throws IOException 
     */
    public static void checkLayout(final WebDriver driver, final String fileName, final String[]includedTags, final String[]excludedTags) throws IOException {
        final GalenPageActionCheck action = new GalenPageActionCheck();
        action.setSpecs(Arrays.asList(fileName));
        if (includedTags != null) {
            action.setIncludedTags(Arrays.asList(includedTags));
        }
        if (excludedTags != null) {
            action.setExcludedTags(Arrays.asList(excludedTags));
        }

        final TestSession session = TestSession.current();
        if (session == null) {
            throw new UnregisteredTestSession("Cannot check layout as there was no TestSession created");
        }

        final TestReport report = session.getReport();
        final CompleteListener listener = session.getListener();
        action.execute(report, new SeleniumBrowser(driver), null, listener);
    }
    


    public static File takeScreenshot(final WebDriver driver) throws IOException {
        final File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        if (GalenConfig.getConfig().shouldAutoresizeScreenshots()) {
            BufferedImage image = Rainbow4J.loadImage(file.getAbsolutePath());

            final Path path = Files.createTempFile("screenshot", ".png");
            final File newFile = path.toFile();
            image = GalenUtils.resizeScreenshotIfNeeded(driver, image);

            Rainbow4J.saveImage(image, newFile);
            // remove temp file when VM terminates
            newFile.deleteOnExit();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Files.delete(path);
                        LOG.debug("deleted file at " + path);
                    } catch (final IOException e) {
                        LOG.error("Unkown error during deleting temporary file.", e);
                    }
                }
            });
            return newFile;
        } else {
            return file;
        }
    }
    
    public static Properties loadProperties(final String fileName) throws IOException {
        
        GalenProperties properties = null;
        if (TestSession.current() != null) {
            properties = TestSession.current().getProperties();
        } else {
            properties = new GalenProperties();
        }
        
        properties.load(new File(fileName));
        return properties.getProperties();
    }
    
    public static void cookie(final WebDriver driver, final String cookie) {
        final String script = "document.cookie=\"" + StringEscapeUtils.escapeJava(cookie) + "\";";
        injectJavascript(driver, script);
    }
    
    public static Object injectJavascript(final WebDriver driver, final String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }
    
    public static String readFile(final String fileName) throws IOException {
        return FileUtils.readFileToString(new File(fileName));
    }
    
    public static Object[] listToArray(final List<?> list) {
        if (list == null) {
            return new Object[]{};
        }
        final Object[] arr = new Object[list.size()];
        return list.toArray(arr);
    }

    public static String getParentForFile(final String filePath) {
        if (filePath != null) {
            return new File(filePath).getParent();
        } else {
            return null;
        }
    }

    public static InputStream findFileOrResourceAsStream(String filePath) throws FileNotFoundException {
        final File file = new File(filePath);

        if (file.exists()) {
            return new FileInputStream(file);
        }
        else {
            if (!filePath.startsWith("/")) {
                filePath = "/" + filePath;
            }
            final InputStream stream = GalenUtils.class.getResourceAsStream(filePath);
            if (stream != null) {
                return stream;
            }
            else {
                final String windowsFilePath = filePath.replace("\\", "/");
                return GalenUtils.class.getResourceAsStream(windowsFilePath);
            }
        }
    }

    public static String calculateFileId(final String fullPath) throws NoSuchAlgorithmException, FileNotFoundException {
        final String fileName = new File(fullPath).getName();
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final InputStream is = GalenUtils.findFileOrResourceAsStream(fullPath);
        new DigestInputStream(is, md);
        final byte [] hashBytes = md.digest();
        
        return fileName + convertHashBytesToString(hashBytes);
    }

    private static String convertHashBytesToString(final byte[] hashBytes) {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : hashBytes) {
            builder.append(Integer.toHexString(0xFF & b));
        }
        return builder.toString();
    }


    public static Pattern convertObjectNameRegex(final String regex) {
        final String jRegex = regex.replace("#", "[0-9]+").replace("*", ".*");
        return Pattern.compile(jRegex);
    }
}
