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
package net.mindengine.galen.specs.reader.page;

import static net.mindengine.galen.suite.reader.Line.UNKNOWN_LINE;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;

import net.mindengine.galen.parser.MathParser;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.Place;
import net.mindengine.galen.specs.reader.SpecReader;

public class StateDoingSection extends State {

    public static final String ONLY_WARN_SYMBOL = "%";
    private final PageSection section;
    private ObjectSpecs currentObjectSpecs;
    private final SpecReader specReader;
    private String[] toParameterize;
    private Parameterization currentParameterization = null;
    private int currentIndentationLevel;
    private String contextPath = ".";
    private PageSpecReader pageSpecReader;
    private Place place;

    public void setCurrentObject(final ObjectSpecs objectSpecs) {
        this.currentObjectSpecs = objectSpecs;
    }

    private class Parameterization {
        private final String[] parameters;
        private final ObjectSpecs[] objectSpecs;

        public Parameterization(final String[] parameters, final ObjectSpecs[] objectSpecs) {
            this.parameters = parameters;
            this.objectSpecs = objectSpecs;
        }

        public void processObject(final String line) throws IOException {
            for (int i = 0; i < parameters.length; i++) {
                final String specText = convertParameterizedLine(line, parameters[i]).trim();
                objectSpecs[i].getSpecs().add(StateDoingSection.this.readSpec(specText, getContextPath(), place));
            }
        }
    }

    private Spec readSpec(String specText, final String contextPath, final Place place) throws IOException {

        boolean onlyWarn = false;
        if (specText.startsWith(ONLY_WARN_SYMBOL)) {
            specText = specText.substring(1);
            onlyWarn = true;
        }
        final Spec spec = specReader.read(specText, contextPath, place);
        spec.setOnlyWarn(onlyWarn);

        return spec;
    }

    public StateDoingSection(final Properties properties, final PageSection section, final String contextPath, final PageSpecReader pageSpecReader) {
        this.section = section;
        this.contextPath = contextPath;
        this.setPageSpecReader(pageSpecReader);
        this.specReader = new SpecReader(properties);
    }

    @Override
    public void process(final VarsContext varsContext, String line, final Place place) throws IOException {
        line = varsContext.process(line);

        this.place = place;
        if (startsWithIndentation(line)) {
            if (line.trim().startsWith("|")) {
                if (currentObjectSpecs == null) {
                    throw new SyntaxException("There was no object defined before this rule");
                }
                processRule(varsContext, line.trim().substring(1).trim(), currentObjectSpecs);
            } else if (currentParameterization != null) {
                currentParameterization.processObject(line);
            } else {
                processSpecForSimpleObject(varsContext, line);
            }
        } else {
            if (line.trim().startsWith("|")) {
                processRule(varsContext, line.trim().substring(1).trim(), null);
            } else if (toParameterize != null) {
                beginParameterizedObject(line, toParameterize);
                toParameterize = null;
            } else {
                beginNewObject(line);
                currentParameterization = null;
            }
        }
    }

    private void processRule(final VarsContext originalVarsContext, final String ruleText, final ObjectSpecs object) throws IOException {
        final VarsContext varsContext = originalVarsContext.copy();

        if (object != null) {
            varsContext.getProperties().setProperty("objectName", object.getObjectName());
        }

        for (final PageSpecRule rule : getPageSpecReader().getRules()) {
            final Matcher matcher = rule.getRule().getPattern().matcher(ruleText);
            if (matcher.matches()) {
                int index = 1;
                for (final String parameterName : rule.getRule().getParameters()) {
                    varsContext.getProperties().setProperty(parameterName, matcher.group(index));
                    index += 1;
                }

                rule.getRuleProcessor().processRule(object, ruleText, varsContext, section, getProperties(), contextPath, pageSpecReader);
                return;
            }
        }
        throw new SyntaxException("There are no rules matching: " + ruleText);
    }

    private void processSpecForSimpleObject(final VarsContext varsContext, final String line) {
        if (currentObjectSpecs == null) {
            throw new SyntaxException(UNKNOWN_LINE, "There is no object defined in section");
        } else {
            try {
                currentObjectSpecs.getSpecs().add(readSpec(line.trim(), getContextPath(), place));
            } catch (final SyntaxException exception) {
                throw exception;
            } catch (final Exception exception) {
                throw new SyntaxException(UNKNOWN_LINE, "Incorrect spec for object \"" + currentObjectSpecs.getObjectName() + "\"", exception);
            }
        }
    }

    private void beginParameterizedObject(final String line, final String[] parameters) {
        final String objectNamePattern = readObjectNameFromLine(line);

        final ObjectSpecs[] objectSpecs = new ObjectSpecs[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            final String objectName = convertParameterizedLine(objectNamePattern, parameters[i]);
            objectSpecs[i] = new ObjectSpecs(objectName);
            section.getObjects().add(objectSpecs[i]);
        }
        currentParameterization = new Parameterization(parameters, objectSpecs);
    }

    private String convertParameterizedLine(final String line, final String parameter) {
        final MathParser parser = new MathParser();
        return parser.parse(line, parameter);
    }

    private void beginNewObject(final String line) {
        final String name = readObjectNameFromLine(line);
        currentObjectSpecs = new ObjectSpecs(name);
        section.getObjects().add(currentObjectSpecs);
    }

    private String readObjectNameFromLine(final String line) {
        final String name = line.replace(":", "").trim();
        return name;
    }

    private boolean startsWithIndentation(final String line) {
        if (line.startsWith("\t")) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect indentation. Should not use tabs. Use spaces");
        }

        final int indentationLevel = getIndentationFrom(line).length();
        if (indentationLevel > 8) {
            throw new SyntaxException(UNKNOWN_LINE, "Incorrect indentation. Use from 1 to 8 spaces for indentation");
        } else if (indentationLevel == 0) {
            currentIndentationLevel = 0;
            return false;
        } else {
            if (currentIndentationLevel > 0) {
                if (currentIndentationLevel != indentationLevel) {
                    throw new SyntaxException(UNKNOWN_LINE, "Incorrect indentation. You should use same indentation within one spec");
                }
            } else {
                currentIndentationLevel = indentationLevel;
            }

            return true;
        }
    }

    private String getIndentationFrom(final String line) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            final char symbol = line.charAt(i);
            if (symbol != ' ') {
                return builder.toString();
            } else {
                builder.append(symbol);
            }
        }

        return builder.toString();
    }

    public void parameterizeNextObject(final String[] parameters) {
        toParameterize = parameters;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(final String contextPath) {
        this.contextPath = contextPath;
    }

    public PageSpecReader getPageSpecReader() {
        return pageSpecReader;
    }

    public void setPageSpecReader(final PageSpecReader pageSpecReader) {
        this.pageSpecReader = pageSpecReader;
    }

}
