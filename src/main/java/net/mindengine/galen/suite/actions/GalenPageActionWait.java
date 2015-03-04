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
package net.mindengine.galen.suite.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionWait extends GalenPageAction {

    private int timeout;
    private List<Until> untilElements;

    public enum UntilType {
        EXIST, GONE, VISIBLE, HIDDEN;

        public static UntilType parseNonStrict(final String text) {
            if ("visible".equals(text)) {
                return VISIBLE;
            } else if ("hidden".equals(text)) {
                return HIDDEN;
            } else if ("exist".equals(text)) {
                return EXIST;
            } else if ("gone".equals(text)) {
                return GONE;
            } else {
                return null;
            }
        }

        @Override
        public String toString() {
            switch (this) {
            case EXIST:
                return "exist";
            case GONE:
                return "gone";
            case VISIBLE:
                return "visible";
            case HIDDEN:
                return "hidden";
            default:
                return "";
            }
        }
    }

    public static class Until {
        private UntilType type;
        private Locator locator;

        public Until(final UntilType type, final Locator locator) {
            this.type = type;
            this.locator = locator;
        }

        public UntilType getType() {
            return type;
        }

        public void setType(final UntilType type) {
            this.type = type;
        }

        public Locator getLocator() {
            return locator;
        }

        public void setLocator(final Locator locator) {
            this.locator = locator;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Until)) {
                return false;
            }

            final Until rhs = (Until) obj;

            return new EqualsBuilder().append(this.type, rhs.type).append(this.locator, rhs.locator).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.type).append(this.locator).toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("type", this.type).append("locator", this.locator).toString();
        }
    }

    @Override
    public void execute(final TestReport report, final Browser browser, final GalenPageTest pageTest, final ValidationListener validationListener)
            throws Exception {
        final Page page = browser.getPage();

        if (untilElements == null || untilElements.isEmpty()) {
            Thread.sleep(timeout);
        } else {
            // waiting for elements
            final int period = 500;
            int tries = timeout / period;
            while (tries-- > 0) {
                Thread.sleep(period);
                if (checkAllConditions(page, null)) {
                    return;
                }
            }

            final StringBuilder results = new StringBuilder();
            if (!checkAllConditions(page, results)) {
                throw new TimeoutException("Failed waiting for:\n" + results.toString());
            }
        }
    }

    private boolean checkAllConditions(final Page page, final StringBuilder result) {

        boolean state = true;

        for (final Until until : untilElements) {

            final PageElement element = page.getObject(until.getLocator());

            if (!checkElement(element, until)) {
                state = false;
                if (result != null) {
                    result.append(" - " + until.getType().toString() + " " + until.getLocator().prettyString() + "\n");
                }
            }
        }
        return state;
    }

    private boolean checkElement(final PageElement element, final Until until) {
        if (until.getType() == UntilType.VISIBLE) {
            return element.isVisible();
        } else if (until.getType() == UntilType.HIDDEN) {
            return !element.isVisible();
        } else if (until.getType() == UntilType.EXIST) {
            return element.isPresent();
        } else if (until.getType() == UntilType.GONE) {
            return !element.isPresent();
        } else {
            return true;
        }
    }

    public GalenPageActionWait withTimeout(final int timeoutInMillis) {
        this.setTimeout(timeoutInMillis);
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("timeout", this.timeout).append("untilElements", this.untilElements).toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GalenPageActionWait)) {
            return false;
        }

        final GalenPageActionWait rhs = (GalenPageActionWait) obj;

        return new EqualsBuilder().append(this.timeout, rhs.timeout).append(this.untilElements, rhs.untilElements).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.timeout).append(this.untilElements).toHashCode();
    }

    public List<Until> getUntilElements() {
        return untilElements;
    }

    public void setUntilElements(final List<Until> untilElements) {
        this.untilElements = untilElements;
    }

    public GalenPageActionWait withUntilElements(final List<Until> list) {
        if (this.untilElements == null) {
            this.untilElements = new LinkedList<GalenPageActionWait.Until>();
        }
        this.untilElements.addAll(list);
        return this;
    }

}
