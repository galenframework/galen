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
package com.galenframework.tests.validation;

import static com.galenframework.specs.Range.*;
import static java.util.Arrays.asList;
import static com.galenframework.specs.Side.LEFT;
import static com.galenframework.specs.Side.RIGHT;
import static com.galenframework.specs.Side.BOTTOM;
import static com.galenframework.specs.Side.TOP;

import java.util.HashMap;
import java.util.List;
import com.galenframework.page.Rect;
import com.galenframework.reports.model.LayoutMeta;
import com.galenframework.specs.*;
import com.galenframework.page.PageElement;
import com.galenframework.validation.ValidationObject;
import org.testng.annotations.DataProvider;

public class InsideValidationTest extends ValidationTestBase {
    public static final List<ValidationObject> NO_AREA = null;

    @Override
    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideGoodSamples() {
        return new Object[][]{
            {specInside("container"), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 20, 100, 100));
                put("container", element(10, 10, 110, 110));
            }})},

            {specInside("container", location(exact(10), RIGHT, TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 20, 100, 100));
                put("container", element(10, 10, 110, 110));
            }})},

            {specInsidePartly("container", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(20, 20, 200, 100));
                put("container", element(10, 10, 110, 110));
            }})},

            {specInside("container", location(between(5, 12), RIGHT, TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 20, 100, 100));
                put("container", element(10, 10, 110, 110));
            }})},

            {specInside("container", location(between(5, 20), LEFT, RIGHT, TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 10, 100, 100));
                put("container", element(5, 5, 120, 120));
            }})},

            {specInside("container", location(exact(5), LEFT), location(between(5, 15), TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 15, 100, 100));
                put("container", element(5, 5, 120, 120));
            }})},

            {specInside("container", location(exact(20).withPercentOf("container/height"), TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 15, 100, 20));
                put("container", element(5, 5, 120, 50));
            }})},

            {specInside("container", location(between(15, 22).withPercentOf("container/height"), TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(10, 15, 100, 20));
                put("container", element(5, 5, 120, 50));
            }})},

            // checking that it allows 2 pixels overlap
            {specInside("container", location(exact(5), TOP)), page(new HashMap<String, PageElement>(){{
                put("object", element(0, 5, 102, 97));
                put("container", element(0, 0, 100, 100));
            }})}
        };
    }

    @SuppressWarnings("serial")
    @DataProvider
    public Object[][] provideBadSamples() {
        return new Object[][]{
            {validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is not completely inside. The offset is 380px."), NULL_META),
                specInside("container"), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 500, 50));
                    put("container", element(0, 0, 130, 120));
                }})
            },

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is not completely inside. The offset is 380px."), NULL_META),
                specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 500, 50));
                    put("container", element(0, 0, 130, 120));
                }})
            },

            {validationResult(areas(new ValidationObject(new Rect(10, 10, 500, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is not completely inside. The offset is 380px."), NULL_META),
                specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(10, 10, 500, 50));
                    put("container", element(0, 0, 130, 120));
                }})
            },

            {validationResult(areas(new ValidationObject(new Rect(190, 110, 500, 500), "object"), new ValidationObject(new Rect(10, 10, 100, 100), "container")),
                    messages("\"object\" is 180px left instead of 10px"),
                    asList(LayoutMeta.distance("object", LEFT, "container", LEFT, "10px", "180px"))),
                specInsidePartly("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(190, 110, 500, 500));
                    put("container", element(10, 10, 100, 100));
                }})
            },

            {validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is 30px left instead of 10px"),
                    asList(LayoutMeta.distance("object", LEFT, "container", LEFT, "10px", "30px"))),
                specInside("container", location(exact(10), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 10, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})
            },

            {validationResult(areas(new ValidationObject(new Rect(30, 20, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is 30px left and 20px top instead of 10px"),
                    asList( LayoutMeta.distance("object", LEFT, "container", LEFT, "10px", "30px"),
                            LayoutMeta.distance("object", TOP, "container", TOP, "10px", "20px") )
            ),
                specInside("container", location(exact(10), LEFT, TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 20, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})
            },

            {validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is 50px right instead of 10px"),
                    asList(LayoutMeta.distance("object", RIGHT, "container", RIGHT, "10px", "50px"))
                ),
                specInside("container", location(exact(10), RIGHT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 10, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})},

            {validationResult(areas(new ValidationObject(new Rect(30, 20, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is 20px top instead of 10px"),
                    asList(LayoutMeta.distance("object", TOP, "container", TOP, "10px", "20px"))
                ),
                specInside("container", location(exact(10), TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 20, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})},

            {validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is 60px bottom instead of 10px"),
                    asList(LayoutMeta.distance("object", BOTTOM, "container", BOTTOM, "10px", "60px"))
                ),
                specInside("container", location(exact(10), BOTTOM)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 10, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})},

            {validationResult(areas(new ValidationObject(new Rect(30, 10, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is 30px left which is not in range of 10 to 20px"),
                    asList(LayoutMeta.distance("object", LEFT, "container", LEFT, "10 to 20px", "30px"))
                ),
                specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 10, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})},

            {validationResult(NO_AREA, messages("Cannot find locator for \"container\" in page spec"), NULL_META),
                specInside("container", location(between(10, 20), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 10, 50, 50));
                }})},

            {validationResult(areas(new ValidationObject(new Rect(30, 5, 50, 50), "object"), new ValidationObject(new Rect(0, 0, 130, 120), "container")),
                    messages("\"object\" is 30px left instead of 10px and 5px top instead of 20px"),
                    asList(
                            LayoutMeta.distance("object", LEFT, "container", LEFT, "10px", "30px"),
                            LayoutMeta.distance("object", TOP, "container", TOP, "20px", "5px")
                    )
                ),
                specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 5, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})},

            {validationResult(areas(new ValidationObject(new Rect(30, 5, 10, 50), "object"), new ValidationObject(new Rect(0, 0, 50, 120), "container")),
                    messages("\"object\" is 60% [30px] left instead of 20% [10px]"), NULL_META),
                specInside("container", location(exact(20).withPercentOf("container/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 5, 10, 50));
                    put("container", element(0, 0, 50, 120));
                }})},

            {validationResult(areas(new ValidationObject(new Rect(30, 5, 10, 50), "object"), new ValidationObject(new Rect(0, 0, 50, 120), "container")),
                    messages("\"object\" is 60% [30px] left which is not in range of 20 to 40% [10 to 20px]"), NULL_META),
                specInside("container", location(between(20, 40).withPercentOf("container/width"), LEFT)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 5, 10, 50));
                    put("container", element(0, 0, 50, 120));
                }})},

            {validationResult(NO_AREA, messages("\"object\" is absent on page"), NULL_META),
                specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", absentElement(30, 5, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})},

            {validationResult(NO_AREA, messages("\"object\" is not visible on page"), NULL_META),
                specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", invisibleElement(30, 5, 50, 50));
                    put("container", element(0, 0, 130, 120));
                }})},

            {validationResult(NO_AREA, messages("\"container\" is absent on page"), NULL_META),
                specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 5, 50, 50));
                    put("container", absentElement(0, 0, 130, 120));
                }})},

            {validationResult(NO_AREA, messages("\"container\" is not visible on page"), NULL_META),
                specInside("container", location(exact(10), LEFT), location(exact(20), TOP)), page(new HashMap<String, PageElement>(){{
                    put("object", element(30, 5, 50, 50));
                    put("container", invisibleElement(0, 0, 130, 120));
                }})}
        };
    }

    private SpecInside specInside(String parentObjectName, Location...locations) {
        return new SpecInside(parentObjectName, asList(locations));
    }

    private SpecInside specInsidePartly(String parentObjectName, Location...locations) {
        return new SpecInside(parentObjectName, asList(locations)).withPartlyCheck();
    }

}
