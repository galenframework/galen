/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.generator.math.Rect;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PageItemJsonMapper {
    private ObjectMapper mapper = new ObjectMapper();

    public List<PageItem> loadItems(InputStream stream) throws IOException {
        JsonNode jsonTree = mapper.readTree(stream);
        List<PageItem> items = new LinkedList<>();
        jsonTree.get("items").fields().forEachRemaining(itemEntry -> {
            items.add(new PageItem(itemEntry.getKey(), readArea(itemEntry.getValue())));
        });

        return items;
    }

    private Rect readArea(JsonNode value) {
        JsonNode areaArray = value.get("area");
        return new Rect(areaArray.get(0).asInt(), areaArray.get(1).asInt(), areaArray.get(2).asInt(), areaArray.get(3).asInt());
    }

}
