package com.galenframework.ocr;

/**
 * Facade object that return the OcrService
 * Currently Google Vision service is the default.
 * A different service can be set.
 * @author guy arieli
 *
 */
public class OcrFacade {
	private static OcrFacade facade = new OcrFacade();
	public static OcrFacade getInstance() {
		return facade;
	}
	private OcrService service = null;
	private OcrFacade() {
	}
	
	public OcrService getOcrService() {
		if(service == null) {
			return new GoogleVisionOcrService();
		} else {
			return service;
		}
	}
	public void setOcrService(OcrService service) {
		this.service = service;
	}
}
