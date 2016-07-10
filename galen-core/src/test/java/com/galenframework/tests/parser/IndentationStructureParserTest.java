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
package com.galenframework.tests.parser;

import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.parser.IndentationStructureParser;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.suite.reader.Line;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IndentationStructureParserTest {

    @Test
    public void shouldRead_structuresFromFile() throws IOException {
        IndentationStructureParser parser = new IndentationStructureParser();


        String content = FileUtils.readFileToString(new File(getClass().getResource("/indentation-structure-parser/struct1.txt").getFile()));
        String contentWithTabs = content.replace("\\t", "\t");
        List<StructNode> nodes = parser.parse(contentWithTabs);

        assertThat(nodes, is(asList(
            node("Node A 0", unknownLine(6)),
            node("Node A 1", unknownLine(8), asList(
                node("Node A 1 1", unknownLine(9), asList(
                    node("Node A 1 1 1", unknownLine(10)),
                    node("Node A 1 1 2", unknownLine(11))
                )),
                node("Node A 1 2", unknownLine(12), asList(
                    node("Node A 1 2 1", unknownLine(13))
                ))
            )),
            node("Node B 1", unknownLine(18), asList(
                node("Node B 1 1", unknownLine(19), asList(
                    node("Node B 1 1 1", unknownLine(20))
                )),
                node("Node B 1 2", unknownLine(21)),
                node("Node B 1 3", unknownLine(22))
            ))
        )));
    }

    private Line unknownLine(int number) {
        return new Line("<unknown>", number);
    }

    @Test(expectedExceptions = SyntaxException.class,
        expectedExceptionsMessageRegExp = "\\QInconsistent indentation\\E\n    in <unknown>:[0-9]+",
        dataProvider = "provideWrongIndentSamples")
    public void shouldGiveError_forInconsistentIndentation(String filePath) throws IOException {
        IndentationStructureParser parser = new IndentationStructureParser();
        String content = FileUtils.readFileToString(new File(getClass().getResource(filePath).getFile()));
        parser.parse(content);
    }

    @DataProvider
    public Object[][] provideWrongIndentSamples() {
        return new Object[][] {
                {"/indentation-structure-parser/struct-wrong-indent.txt"},
                {"/indentation-structure-parser/struct-wrong-indent-2.txt"}
        };
    }

    private StructNode node(String name, Line line) {
        StructNode node = new StructNode(name);
        node.setLine(line);
        return node;
    }
    private StructNode node(String name, Line line, List<StructNode> childNodes) {
        StructNode node = new StructNode(name);
        node.setLine(line);
        node.setChildNodes(childNodes);
        return node;
    }
}
