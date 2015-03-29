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
package net.mindengine.galen.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.mindengine.galen.browser.Browser;
import net.mindengine.rainbow4j.Rainbow4J;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageDump {

    private final static Logger LOG = LoggerFactory.getLogger(PageDump.class);

    private String pageName;
    private String title;
    private Map<String, Element> items = new HashMap<String, Element>();

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addElement(Element element) {
        items.put(element.getObjectName(), element);
    }

    public Map<String, Element> getItems() {
        return items;
    }

    public void setItems(Map<String, Element> items) {
        this.items = items;
    }

    public void exportAsJson(File file) throws IOException {
        makeSureFileExists(file);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file, this);
    }

    public void exportAsHtml(String title, File file) throws IOException {
        makeSureFileExists(file);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonText = objectMapper.writeValueAsString(this);

        String template = IOUtils.toString(getClass().getResourceAsStream("/pagedump/page.html"));

        String htmlText = template.replace("${title}", title);
        htmlText = htmlText.replace("${json}", jsonText);


        FileUtils.writeStringToFile(file, htmlText);
    }

    public void makeSureFileExists(File file) throws IOException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Couldn't create file: " + file.getAbsolutePath());
            }
        }
    }

    public void exportAllScreenshots(Browser browser, File reportFolder) throws IOException {

        File screenshotOriginalFile = browser.createScreenshot();

        FileUtils.copyFile(screenshotOriginalFile, new File(reportFolder.getAbsolutePath() + File.separator + "page.png"));

        BufferedImage image = Rainbow4J.loadImage(screenshotOriginalFile.getAbsolutePath());


        File objectsFolder = new File(reportFolder.getAbsolutePath() + File.separator + "objects");
        objectsFolder.mkdirs();

        for (Element element : items.values()) {
            if (element.hasImage) {
                int[] area = element.getArea();

                try {
                    BufferedImage subImage = image.getSubimage(area[0], area[1], area[2], area[3]);
                    Rainbow4J.saveImage(subImage, new File(objectsFolder.getAbsolutePath() + File.separator + element.getObjectName() + ".png"));
                }
                catch (Exception ex) {
                    LOG.error("Got error during saving image", ex);
                }
            }
        }
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public static class Element {

        @JsonIgnore
        private String objectName;
        private int[] area;
        private String text;
        private boolean hasImage = false;

        public Element(String objectName, int[] area, String text) {
            setObjectName(objectName);
            setArea(area);
            setText(text);
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public String getObjectName() {
            return objectName;
        }

        public void setArea(int[] area) {
            this.area = area;
        }

        public int[] getArea() {
            return area;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setHasImage(boolean hasImage) {
            this.hasImage = hasImage;
        }

        public boolean getHasImage() {
            return hasImage;
        }
    }
}
