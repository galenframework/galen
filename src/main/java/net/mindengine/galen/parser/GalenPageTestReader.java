package net.mindengine.galen.parser;

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
        try {
            String[] args = net.mindengine.galen.parser.CommandLineParser.parseCommandLine(text);
            if (args.length == 0) {
                throw new SuiteParserException("Cannot parse page: " + text);
            }
            
            if (isUrl(args[0])) {
                if (args.length < 2) {
                    throw new SuiteParserException("You should specify screen size");
                }
                return defaultGalenPageTest(args[0], args[1]);
            }
            else {
                String first = args[0].toLowerCase();
                if (first.equals("selenium")) {
                    return seleniumGalenPageTest(args);
                }
                else throw new SuiteParserException("Unknown browser factory: " + first);
            }
        }
        catch (Exception e) {
            throw new SuiteParserException("Error parsing: " + text, e);
        }
    }
    private static GalenPageTest seleniumGalenPageTest(String[] args) {
        if (args.length < 4) {
            throw new SuiteParserException("Incorrect amount of arguments");
        }
        String seleniumType = args[1].toLowerCase();
        if ("grid".equals(seleniumType)) {
            return gridGalenPageTest(args);
        }
        else {
            return seleniumSimpleGalenPageTest(seleniumType, args[2], args[3]);
        }
    }
    private static GalenPageTest gridGalenPageTest(String[] args) {
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
                throw new SuiteParserException("Couldn't parse grid endpoint");
            }
            
            
            String gridUrl = args[2];
            String pageUrl = cmd.getOptionValue("u");
            if (pageUrl == null) {
                throw new SuiteParserException("Page url is not specified");
            }
            
            String size = cmd.getOptionValue("s");
            if (size == null) {
                throw new SuiteParserException("Size is not specified");
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
            throw new SuiteParserException("Couldn't parse grid arguments", e);
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
