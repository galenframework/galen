package net.mindengine.galen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class GalenConfig {
    
    private final static GalenConfig instance = new GalenConfig();
    private Integer rangeApproximation;
    private List<String> reportingListeners;
    
    private GalenConfig() {
        try {
            loadConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadConfig() throws IOException {
        Properties prop = new Properties();
        File configFile = new File("config");
        
        if (configFile.exists()) {
            InputStream in = new FileInputStream(configFile);
            prop.load(in);
            in.close();
        }
        
        rangeApproximation = Integer.parseInt(readProperty(prop, "galen.range.approximation", "2"));
        reportingListeners = converCommaSeparatedList(readProperty(prop, "galen.reporting.listeners", ""));
        
    }

    private List<String> converCommaSeparatedList(String text) {
        String[] arr = text.split(",");
        
        List<String> list = new LinkedList<String>();
        for (String item : arr) {
            String itemText = item.trim();
            if (!itemText.isEmpty()) {
                list.add(itemText);
            }
        }
        return list;
    }

    private String readProperty(Properties prop, String name, String defaultValue) {
        return prop.getProperty(name, System.getProperty(name, defaultValue));
    }

    public synchronized static GalenConfig getConfig() {
        return instance;
    }
    
    public void reset() throws IOException {
        loadConfig();
    }

    public Integer getRangeApproximation() {
        return this.rangeApproximation;
    }

    public List<String> getReportingListeners() {
        return this.reportingListeners;
    }

}
