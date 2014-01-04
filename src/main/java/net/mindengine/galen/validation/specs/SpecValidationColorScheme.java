package net.mindengine.galen.validation.specs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.SpecColorScheme;
import net.mindengine.galen.specs.colors.ColorRange;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.PageValidation;
import net.mindengine.galen.validation.SpecValidation;
import net.mindengine.galen.validation.ValidationErrorException;
import net.mindengine.rainbow4j.Rainbow4J;
import net.mindengine.rainbow4j.Spectrum;

public class SpecValidationColorScheme extends SpecValidation<SpecColorScheme> {

    @Override
    public void check(PageValidation pageValidation, String objectName, SpecColorScheme spec) throws ValidationErrorException {
        PageElement mainObject = pageValidation.findPageElement(objectName);
        checkAvailability(mainObject, objectName);

        
        BufferedImage pageImage = pageValidation.getPage().getScreenshotImage();
        
        Rect area = mainObject.getArea();
        if (pageImage.getWidth() < area.getLeft() + area.getWidth() || pageImage.getHeight() < area.getTop() + area.getHeight()) {
            throw new ValidationErrorException()
                .withErrorArea(new ErrorArea(area, objectName))
                .withMessage("Can't fetch image for \"object\" as it is outside of screenshot");
        }
        
        BufferedImage objectImage = Rainbow4J.crop(pageImage, area.getLeft(), area.getTop(), area.getWidth(), area.getHeight());
        
        Spectrum spectrum;
        try {
            spectrum = Rainbow4J.readSpectrum(objectImage, 128);
        } catch (Exception e) {
            throw new ValidationErrorException(String.format("Couldn't fetch spectrum for \"%s\"", objectName));
        }
        
        List<String> messages = new LinkedList<String>();
        
        for (ColorRange colorRange : spec.getColorRanges()) {
            Color color = colorRange.getColor();
            double percentage = spectrum.getPercentage(color.getRed(), color.getGreen(), color.getBlue(), 20);
            
            if (!colorRange.getRange().holds(percentage)) {
                messages.add(String.format("color %s on \"%s\" is %d%% %s", toHexColor(color), objectName, (int)percentage, colorRange.getRange().getErrorMessageSuffix("%")));
            }
        }
        
        if (messages.size() > 0) {
            throw new ValidationErrorException()
                    .withErrorArea(new ErrorArea(area, objectName))
                    .withMessages(messages);
        }
    }

    private String toHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
