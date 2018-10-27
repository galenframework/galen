package com.galenframework.tests.validation;

import com.galenframework.components.validation.MockedPage;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.ocr.OcrResult;
import com.galenframework.ocr.OcrService;
import com.galenframework.page.PageElement;
import com.galenframework.page.Rect;
import com.galenframework.rainbow4j.Rainbow4J;
import com.galenframework.specs.SpecOcr;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationErrorException;
import com.galenframework.validation.ValidationResult;
import com.galenframework.validation.specs.SpecValidationOcr;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OcrValidationTest {

    private final OcrService ocrService = mock(OcrService.class);
    private final SpecValidationOcr ocrValidation = new SpecValidationOcr(ocrService);
    private BufferedImage fakeScreenshot = loadTestImage("/imgs/page-screenshot.png");

    @Test
    public void should_fail_check_when_text_is_different() throws Exception {
        when(ocrService.findOcrText(any(), any())).thenReturn(
                new OcrResult("  Real text  \n  ", new Rect(0,0, 100, 50))
        );

        SpecOcr spec = new SpecOcr(SpecOcr.Type.IS, "Expected text");

        MockedPage page = createMockedPage();
        PageSpec pageSpec = createMockedPageSpec(page);
        PageValidation pageValidation = new PageValidation(null, page, pageSpec, null, null);

        try {
            ocrValidation.check(pageValidation, "button", spec);
            throw new RuntimeException("It didn't throw exception but should");
        } catch(ValidationErrorException ex) {
            assertThat(ex.getErrorMessages(), is(asList("\"button\" text is \"Real text\" but should be \"Expected text\"")));
        }


        verify(ocrService).findOcrText(anyObject(), eq(new Rect(0, 0, 100, 50)));
    }

    private PageSpec createMockedPageSpec(MockedPage page) {
        PageSpec pageSpec = new PageSpec();

        for (String objectName : page.getElements().keySet()) {
            pageSpec.getObjects().put(objectName, new Locator("id", objectName));
        }
        return pageSpec;
    }

    private MockedPage createMockedPage() {
        MockedPage page = new MockedPage(new HashMap<String, PageElement>() {{
            put("button", new MockedPageElement(0, 0, 100, 50));
        }});

        page.setScreenshotImage(fakeScreenshot);
        return page;
    }

    private BufferedImage loadTestImage(String imagePath) {
        try {
            return Rainbow4J.loadImage(getClass().getResource(imagePath).getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
