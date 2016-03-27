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
package com.galenframework.rainbow4j;

import java.nio.ByteBuffer;

/**
 * Created by hypery2k on 09.02.16.
 */
public class BufferUtils {

    public static ByteBuffer clone(ByteBuffer original) {
        ByteBuffer clone = ByteBuffer.allocateDirect(original.capacity());
        original.rewind();//copy from the beginning
        clone.put(original);
        original.rewind();
        clone.flip();
        return clone;
    }
}
