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
package net.mindengine.galen.reports.model;

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
    private Map<String, File> files = new HashMap<String, File>();
    private List<FileTempStorage> childStorages = new LinkedList<FileTempStorage>();

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
            // remove temp file after copy
            FileUtils.deleteQuietly(entry.getValue());
        }

        for (FileTempStorage storage : childStorages) {
            storage.copyAllFilesTo(dir);
        }
    }

    public void registerStorage(FileTempStorage fileStorage) {
        this.childStorages.add(fileStorage);
    }
}
