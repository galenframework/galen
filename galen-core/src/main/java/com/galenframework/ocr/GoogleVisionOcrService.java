package com.galenframework.ocr;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.ocr.google.ModelUtils;
import com.galenframework.ocr.google.pojo.GoogleModel;
import com.galenframework.ocr.google.pojo.request.Feature;
import com.galenframework.ocr.google.pojo.request.GoogleRequest;
import com.galenframework.ocr.google.pojo.request.Image;
import com.galenframework.ocr.google.pojo.request.Request;
import com.galenframework.page.Rect;
import com.galenframework.validation.ValidationErrorException;
import com.google.gson.Gson;

/**
 * Implementation of the OcrService.
 * Cache the model, so if the same image is used, will send a single OCR request to the service.
 * @author guy arieli
 *
 */
public class GoogleVisionOcrService implements OcrService {
	private final static String BASE_URL = "https://vision.googleapis.com/v1/images:annotate?key=";
	private GoogleModel model = null;
	private BufferedImage lastImage = null;

	@Override
	public OcrResult findOcrText(BufferedImage img, Rect rec) throws ValidationErrorException {
		if (!(img == lastImage)) { // check if the current model is valid.
			try {
				model = getGoogleModel(img);
			} catch (Exception e) {
				throw new ValidationErrorException("Google vision API error", e);
			}
			lastImage = img;
		}
		return ModelUtils.findTextInArea(model, rec);
	}

	public static GoogleModel getGoogleModel(BufferedImage img) throws Exception {
		String key = GalenConfig.getConfig().readProperty(GalenProperty.GALEN_GOOGLE_VISION_KEY);
		if (key == null) {
			throw new RuntimeException(
					"To use the OCR you need to configure your .galen.config file with your API key:\ngoogle.vision.key=<YOUR KEY>\nhttps://cloud.google.com/vision/docs/auth");
		}
		GoogleRequest grequest = new GoogleRequest();
		List<Request> requests = new ArrayList<>();
		Request request = new Request();
		requests.add(request);
		Image image = new Image();
		image.setContent(imgToBase64String(img, "PNG"));
		request.setImage(image);
		grequest.setRequests(requests);
		List<Feature> features = new ArrayList<>();
		Feature feature = new Feature();
		feature.setType("TEXT_DETECTION");
		feature.setMaxResults(1);
		request.setFeatures(features);
		features.add(feature);

		String result = RestUtils.executePost(BASE_URL + key, new Gson().toJson(grequest));
		return new Gson().fromJson(result, GoogleModel.class);
	}

	public static String imgToBase64String(final RenderedImage img, final String formatName) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			ImageIO.write(img, formatName, os);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}
}
