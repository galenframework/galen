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

import com.galenframework.page.Rect;
/**
 * Retrun the text as well as the rectangle of the text (not the element but just the text).
 * @author guy arieli
 *
 */
public class OcrResult {
	private String text;
	private Rect rect;

	public OcrResult(String text, Rect rect) {
	    this.text = text;
	    this.rect = rect;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Rect getRect() {
		return rect;
	}
	public void setRect(Rect rect) {
		this.rect = rect;
	}
}
