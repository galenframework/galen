package net.mindengine.galen.parser;

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;
import static net.mindengine.galen.utils.GalenUtils.isUrl;
import static net.mindengine.galen.utils.GalenUtils.readSize;
import net.mindengine.galen.browser.SeleniumBrowserFactory;
import net.mindengine.galen.browser.SeleniumGridBrowserFactory;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.utils.GalenUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.openqa.selenium.Platform;

public class GalenPageTestReader {

    public static GalenPageTest readFrom(String text) {
        String[] args = net.mindengine.galen.parser.CommandLineParser.parseCommandLine(text);
        if (args.length == 0) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect amount of arguments: " + text.trim());
        }
        
        if (isUrl(args[0])) {
            if (args.length < 2) {
                throw new SyntaxException(UNKNOWN_LINE, "You should specify screen size");
            }
            return defaultGalenPageTest(args[0], args[1]);
        }
        else {
            String first = args[0].toLowerCase();
            if (first.equals("selenium")) {
                return seleniumGalenPageTest(args, text.trim());
            }
            else throw new SyntaxException(UNKNOWN_LINE, "Unknown browser factory: " + first);
        }
    }
    private static GalenPageTest seleniumGalenPageTest(String[] args, String originalText) {
        if (args.length < 4) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect amount of arguments: " + originalText);
        }
        String seleniumType = args[1].toLowerCase();
        if ("grid".equals(seleniumType)) {
            return gridGalenPageTest(args, originalText);
        }
        else {
            return seleniumSimpleGalenPageTest(seleniumType, args[2], args[3]);
        }
    }
    private static GalenPageTest gridGalenPageTest(String[] args, String originalText) {
        Options options = new Options();
        options.addOption("b", "browser", true, "browser name");
        options.addOption("v", "version", true, "browser version");
        options.addOption("p", "platform", true, "platform name");
        options.addOption("u", "page", true, "page url");
        options.addOption("s", "size", true, "size");
        
        CommandLineParser parser = new PosixParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            String[] gridArgs = cmd.getArgs();
            
            if (gridArgs.length < 3) {
                throw new SyntaxException(UNKNOWN_LINE, "Couldn't parse grid endpoint: " + originalText);
            }
            
            
            String gridUrl = args[2];
            String pageUrl = cmd.getOptionValue("u");
            if (pageUrl == null) {
                throw new SyntaxException(UNKNOWN_LINE, "Page url is not specified: " + originalText);
            }
            
            String size = cmd.getOptionValue("s");
            if (size == null) {
                throw new SyntaxException(UNKNOWN_LINE, "Size is not specified: " + originalText);
            }
            
            return new GalenPageTest()
                .withUrl(pageUrl)
                .withSize(readSize(size))
                .withBrowserFactory(new SeleniumGridBrowserFactory(gridUrl)
                    .withBrowser(cmd.getOptionValue("b"))
                    .withBrowserVersion(cmd.getOptionValue("v"))
                    .withPlatform(readPlatform(cmd.getOptionValue("p")))
                );
           
        }
        catch (ParseException e) {
            throw new SyntaxException(UNKNOWN_LINE, "Couldn't parse grid arguments: " + originalText, e);
        }
    }
    private static Platform readPlatform(String platformText) {
        if (platformText == null) {
            return null;
        }
        else return Platform.valueOf(platformText.toUpperCase());
    }
    private static GalenPageTest seleniumSimpleGalenPageTest(String browser, String url, String screenSize) {
        return new GalenPageTest()
            .withUrl(url)
            .withSize(GalenUtils.readSize(screenSize))
            .withBrowserFactory(new SeleniumBrowserFactory(browser));
    }
    private static GalenPageTest defaultGalenPageTest(String url, String sizeText) {
        return new GalenPageTest()
            .withUrl(url)
            .withSize(GalenUtils.readSize(sizeText))
            .withBrowserFactory(new SeleniumBrowserFactory());
    }
}
