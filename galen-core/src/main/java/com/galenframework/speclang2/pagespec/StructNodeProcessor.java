package com.galenframework.speclang2.pagespec;

import com.galenframework.parser.StringCharReader;
import com.galenframework.parser.StructNode;

import java.io.IOException;
import java.util.List;

public interface StructNodeProcessor {
    List<StructNode> process(StringCharReader reader, StructNode structNode) throws IOException;
}
