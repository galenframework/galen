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
package net.mindengine.galen.tests.parser;

import net.mindengine.galen.parser.IndentationStructureParser;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
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
                node("Node A 0"),
                node("Node A 1", asList(
                        node("Node A 1 1", asList(
                                node("Node A 1 1 1"),
                                node("Node A 1 1 2")
                        )),
                        node("Node A 1 2", asList(
                                node("Node A 1 2 1")
                        ))
                )),
                node("Node B 1", asList(
                        node("Node B 1 1", asList(
                                node("Node B 1 1 1")
                        )),
                        node("Node B 1 2"),
                        node("Node B 1 3")
                ))
        )));
    }

    @Test(expectedExceptions = SyntaxException.class,
        expectedExceptionsMessageRegExp = "Inconsistent indentation",
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

    private StructNode node(String name) {
        return new StructNode(name);
    }
    private StructNode node(String name, List<StructNode> childNodes) {
        StructNode node =  new StructNode(name);
        node.setChildNodes(childNodes);
        return node;
    }
}
