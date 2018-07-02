package com.galenframework.ocr.google;

import java.util.ArrayList;
import java.util.List;

import com.galenframework.ocr.OcrResult;
import com.galenframework.ocr.google.pojo.GoogleModel;
import com.galenframework.ocr.google.pojo.TextAnnotation;
import com.galenframework.ocr.google.pojo.Vertex;
import com.galenframework.page.Point;
import com.galenframework.page.Rect;

public class ModelUtils {
	/**
	 * Handle the the model, extract the text in a specific area in the model. 
	 * @param model The returned model
	 * @param area The area to search text in.
	 * @return
	 */
	public static OcrResult findTextInArea(GoogleModel model, Rect area) {
		OcrResult result = new OcrResult();
		result.setRect(area);
		List<TextAnnotation> annotations = model.getResponses().get(0).getTextAnnotations();
		ArrayList<TextAnnotation> inAreaAnnotations = new ArrayList<>();
		for(TextAnnotation anno: annotations) {
			if(anno.getDescription().contains("\n")) {
				continue;
			}
			boolean in = true;
			for(Vertex vertex:  anno.getBoundingPoly().getVertices()) {
				if(!area.contains(new Point(vertex.getX(), vertex.getY()))) {
					in = false;
					break;
				}
			}
			if(in) {
				inAreaAnnotations.add(anno);
			}
		}
		if(inAreaAnnotations.isEmpty()) {
			result.setText("");
			return result;
		}
		StringBuffer text = new StringBuffer();
		int lineButtom = -1;
		int minX = -1;
		int minY = -1;
		int maxX = -1;
		int maxY = -1;
		// Create lines out of separated words
		for(TextAnnotation anno: inAreaAnnotations) {
			int currentMaxY = anno.getBoundingPoly().getVertices().get(3).getY();
			int currentMinY = anno.getBoundingPoly().getVertices().get(0).getY();
			int currentMaxX = anno.getBoundingPoly().getVertices().get(1).getX();
			int currentMinX = anno.getBoundingPoly().getVertices().get(0).getX();
			if(lineButtom == -1) {
				lineButtom = currentMaxY;
			}
			if(currentMinY > lineButtom) {
				text.append("\n");
				lineButtom = currentMaxY;
			}
			if(currentMaxY > lineButtom) {
				lineButtom = currentMaxY;
			}
			text.append(anno.getDescription());
			text.append(" ");
			if(minX == -1) {
				minX = currentMinX;
				minY = currentMinY;
				maxX = currentMaxX;
				maxY = currentMaxY;
			} else {
				if(currentMinX < minX) {
					minX = currentMinX;
				}
				if(currentMaxX > maxX) {
					maxX = currentMaxX;
				}
				if(currentMinY < minY) {
					minY = currentMinY;
				}
				if(currentMaxY > maxY) {
					maxY = currentMaxY;
				}
			}
		}
		result.setText(text.toString().trim());
		if(!inAreaAnnotations.isEmpty()) {
			result.setRect(new Rect(minX, minY, maxX - minX, maxY - minY));
		}
		return result;
	}
}
