package com.galenframework.speclang2.pagespec;

import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class LibProcessor extends ImportProcessor {
    private final List<String> embeddedLibraryNames;

    public LibProcessor(PageSpecHandler pageSpecHandler) {
        super(pageSpecHandler);
        embeddedLibraryNames = loadLibraryNamesFrom("/spec-libs/libs.list");
    }

    private List<String> loadLibraryNamesFrom(String path) {
        try {
            String text = IOUtils.toString(getClass().getResourceAsStream(path));
            return asList(text.split("\r\n")).stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load libraries from " + path, e);
        }
    }

    protected List<StructNode> importPageSpec(String filePath, StructNode origin) throws IOException {
        if (!embeddedLibraryNames.contains(filePath)) {
            throw new SyntaxException(origin, "Cannot find library: " + filePath);
        }
        return loadPageSpec(origin, "/spec-libs/" + filePath + "/" + filePath + ".gspec");
    }

}
