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
package com.galenframework.parser;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static java.util.Arrays.asList;

public class CommandLineReader {
    private final List<String> args;
    private final ListIterator<String> it;


    public CommandLineReader(String[] args) {
        this.args = new LinkedList<>(asList(args));
        this.it = this.args.listIterator();
    }

    public boolean hasNext() {
        return it.hasNext();
    }

    public boolean isNextArgument() {
        if (hasNext()) {
            String next = it.next();
            it.previous();
            return (next != null && next.startsWith("--"));

        } else throw new IndexOutOfBoundsException();
    }

    public String readNext() {
        return it.next();
    }

    public Pair<String, String> readArgument() {
        if (hasNext()) {
            String argumentName = convertArgumentName(readNext());
            if (hasNext()) {
                String argumentValue = readNext();
                return new ImmutablePair<>(argumentName, argumentValue);
            } else {
                throw new IndexOutOfBoundsException("Argument '" + argumentName + "' doesn't have a value");
            }

        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private String convertArgumentName(String argument) {
        return argument.substring(2);
    }

    public void skipArgument() {
        readNext();
    }
}
