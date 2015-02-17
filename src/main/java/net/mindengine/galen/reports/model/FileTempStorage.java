package net.mindengine.galen.reports.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishubin on 2/17/15.
 */
public class FileTempStorage {

    private final String storageName;
    private Map<String, File> files = new HashMap<String, File>();

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
}
