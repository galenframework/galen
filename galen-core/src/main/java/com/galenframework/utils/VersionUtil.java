/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://galenframework.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.galenframework.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author hypery2k
 */
public class VersionUtil {

    private final static Logger LOG = LoggerFactory.getLogger(GalenUtils.class);

    public static String getVersion(Class<?> clazz) {
        String implementationVersion = getVersion(clazz, "Implementation-Version");
        String implementationBuild = getVersion(clazz, "Implementation-Build");
        String version = implementationVersion;
        if (StringUtils.isNotBlank(implementationBuild)) {
            version = StringUtils.join(new String[]{implementationVersion, implementationBuild}, '.');
        }
        return version;
    }

    public static String getVersion(Class<?> clazz, String key) {
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            // Class not from JAR
            String relativePath = clazz.getName().replace('.', File.separatorChar) + ".class";
            String classFolder = classPath.substring(0, classPath.length() - relativePath.length() - 1);
            String manifestPath = classFolder + "/META-INF/MANIFEST.MF";
            return readVersionFrom(manifestPath, key);
        } else {
            String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
            return readVersionFrom(manifestPath, key);
        }
    }


    private static String readVersionFrom(String manifestPath, String key) {
        Manifest manifest = null;
        try {
            manifest = new Manifest(new URL(manifestPath).openStream());
            Attributes attrs = manifest.getMainAttributes();
            String version = readFromAttributes(attrs, key);
            if (StringUtils.isEmpty(version)) {
                version = readFromAttributes(manifest.getAttributes("Build-Info"), key);
            }
            return version;
        } catch (Exception e) {
            LOG.debug("Could not read manifest info", e);
        }
        return StringUtils.EMPTY;
    }

    private static String readFromAttributes(Attributes attributes, String key) {
        String value = attributes.getValue(key);
        if (value == null) {
            value = "";
        }
        return StringUtils.replace(value, "-SNAPSHOT", "");
    }
}
