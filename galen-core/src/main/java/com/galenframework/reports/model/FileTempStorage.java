/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.reports.model;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ishubin on 2/17/15.
 */
public class FileTempStorage {

    private final String storageName;
    private Map<String, File> files = new HashMap<>();
    private List<FileTempStorage> childStorages = new LinkedList<>();

    private static long _uniqueId = 0;

    public FileTempStorage(String storageName) {
        this.storageName = storageName;
    }

    private synchronized static long getUniqueId() {
        _uniqueId += 1;
        return _uniqueId;
    }

    public String registerFile(String fileName, File file) {
        String uniqueName = storageName + "-" + getUniqueId() + "-" + fileName;
        files.put(uniqueName, file);
        return uniqueName;
    }


    public Map<String, File> getFiles() {
        return files;
    }

    public void copyAllFilesTo(File dir) throws IOException {
        for (Map.Entry<String, File> entry : files.entrySet()) {
            FileUtils.copyFile(entry.getValue(), new File(dir.getAbsolutePath() + File.separator + entry.getKey()));
        }

        for (FileTempStorage storage : childStorages) {
            storage.copyAllFilesTo(dir);
        }
    }

    public void registerStorage(FileTempStorage fileStorage) {
        this.childStorages.add(fileStorage);
    }

    /**
     * Removes all temporary files from disk.
     * IMPORTANT! Use this call only in the end
     * when you are sure you don't need report files anymore
     */
    public void cleanup() {
        if (this.childStorages != null) {
            for (FileTempStorage storage : this.childStorages) {
                storage.cleanup();
            }
        }

        for (File file : this.files.values()) {
            FileUtils.deleteQuietly(file);
        }
        this.files.clear();
    }
}
