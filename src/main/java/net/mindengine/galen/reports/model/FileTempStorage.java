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
        }

        for (FileTempStorage storage : childStorages) {
            storage.copyAllFilesTo(dir);
        }
    }

    public void registerStorage(FileTempStorage fileStorage) {
        this.childStorages.add(fileStorage);
    }
}
