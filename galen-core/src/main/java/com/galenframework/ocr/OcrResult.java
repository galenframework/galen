package com.galenframework.ocr;

import com.galenframework.page.Rect;
/**
 * Retrun the text as well as the rectangle of the text (not the element but just the text).
 * @author guy arieli
 *
 */
public class OcrResult {
	private String text = null;
	private Rect rect = null;
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
