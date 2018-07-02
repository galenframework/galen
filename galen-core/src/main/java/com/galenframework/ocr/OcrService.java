package com.galenframework.ocr;

import java.awt.image.BufferedImage;

import com.galenframework.page.Rect;
import com.galenframework.validation.ValidationErrorException;
/**
 * Define the interface with 3'rd party OCR services (local / cloud)
 * The method accept an image and rectangle and return the analysis result
 * @author guy arieli
 *
 */
public interface OcrService {
	/**
	 * 
	 * @param img the image to analyze
	 * @param rec the area to search text in
	 * @return the text and the area this text was found in
	 * @throws ValidationErrorException
	 */
	public OcrResult findOcrText(BufferedImage img, Rect rec) throws ValidationErrorException;
}
