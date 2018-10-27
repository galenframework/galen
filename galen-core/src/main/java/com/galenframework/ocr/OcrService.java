/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
	 OcrResult findOcrText(BufferedImage img, Rect rec) throws ValidationErrorException;
}
