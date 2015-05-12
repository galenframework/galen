package net.mindengine.galen.api;

import java.net.MalformedURLException;
import java.util.List;

import net.mindengine.galen.utils.TestDevice;

import org.openqa.selenium.WebDriver;

/**
 * Java API for Galen layout testing. Will be injected to interact with Galen
 * 
 * @author mreinhardt
 *
 */
public interface GalenExecutor {

    /**
     * Check the layout of a page for the given url, device and specs
     * 
     * @param testDevice
     *            device to test against
     * @param url
     *            page to load
     * @param specs
     *            specs to use for validating
     * @throws Exception
     *             in case of any errors
     */
    void checkLayout(TestDevice testDevice, String url, List<String> specs);

    /**
     * Check the layout of a page for the given url, device and specs
     * 
     * @param testDevice
     *            device to test against
     * @param url
     *            page to load
     * @param specs
     *            specs to use for validating
     * @param includedTags
     *            explicit list of tags to test against, if null tags of the used device are used
     * @throws Exception
     *             in case of any errors
     */
    void checkLayout(TestDevice testDevice, String url, List<String> specs, List<String> includedTags);

    /**
     * Check the layout of a page for the given url, device and specs
     * 
     * @param testDevice
     *            device to test against
     * @param url
     *            page to load
     * @param specs
     *            specs to use for validating
     * @param includedTags
     *            explicit list of tags to test against, if null tags of the used device are used
     * @param groups
     *            to show in the report
     * @throws Exception
     *             in case of any errors
     */
    void checkLayout(TestDevice testDevice, String url, List<String> specs, List<String> includedTags, List<String> groups);

    /**
     * Check the layout of a page for the given url, device and spec
     * 
     * @param testDevice
     *            device to test against
     * @param url
     *            page to load
     * @param spec
     *            spec to use for validating
     * @throws Exception
     *             in case of any errors
     */
    void checkLayout(TestDevice testDevice, String url, String spec);

    /**
     * Check the layout of a page for the given url, device and spec
     * 
     * @param testDevice
     *            device to test against
     * @param url
     *            page to load
     * @param specs
     *            spec to use for validating
     * @param includedTags
     *            explicit list of tags to test against, if null tags of the used device are used
     * @throws Exception
     *             in case of any errors
     */
    void checkLayout(TestDevice testDevice, String url, String spec, List<String> includedTags);

    /**
     * Check the layout of a page for the given url, device and spec
     * 
     * @param testDevice
     *            device to test against
     * @param url
     *            page to load
     * @param spec
     *            spec to use for validating
     * @param includedTags
     *            explicit list of tags to test against, if null tags of the used device are used
     * @param groups
     *            to show in the report
     * @throws Exception
     *             in case of any errors
     */
    void checkLayout(TestDevice testDevice, String url, String spec, List<String> includedTags, List<String> groups);

    /**
     * Get the current used driver instance
     * 
     * @return current instance
     * @throws MalformedURLException
     */
    WebDriver getDriverInstance();

    /**
     * Create a WebDriver instance
     * 
     * @return the created driver instance
     * @throws MalformedURLException
     */
    abstract WebDriver createDriver();

    /**
     * Closes the current driver instance
     * 
     */
    void quitDriver();
}
