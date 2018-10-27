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
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.ocr.google.pojo.GoogleModel;
import com.galenframework.ocr.google.pojo.request.Feature;
import com.galenframework.ocr.google.pojo.request.GoogleRequest;
import com.galenframework.ocr.google.pojo.request.Image;
import com.galenframework.ocr.google.pojo.request.Request;
import com.galenframework.page.Rect;
import com.galenframework.validation.ValidationErrorException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

/**
 * Implementation of the OcrService.
 * Cache the model, so if the same image is used, will send a single OCR request to the service.
 * @author guy arieli, Ivan Shubin
 *
 */
public class GoogleVisionOcrService implements OcrService {
	private final static String BASE_URL = "https://vision.googleapis.com/v1/images:annotate?key=";
	final static HttpClient httpClient = HttpClients.createDefault();
	final static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public OcrResult findOcrText(BufferedImage image, Rect rect) throws ValidationErrorException {
        if (rect.getRight() > image.getWidth() && rect.getBottom() > rect.getHeight()) {
            throw new ValidationErrorException("Could not extract element image. Looks like it is located outside of screenshot area");
        }

        try {
            BufferedImage croppedImage = image.getSubimage(rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight());
            GoogleModel model = getGoogleModel(croppedImage);

            if (model.responses != null && !model.responses.isEmpty()) {
                String resultedText = model.responses.get(0).fullTextAnnotation.text;
                if (resultedText == null) {
                    resultedText = "";
                }
                return new OcrResult(new String(resultedText.getBytes(Charset.forName("utf-8"))), rect);
            } else {
                throw new NullPointerException("Got empty result");
            }

        } catch (Exception e) {
            throw new ValidationErrorException("Google vision API error: " + e.getMessage(), e);
        }
	}

	public static GoogleModel getGoogleModel(BufferedImage img) throws Exception {
		String key = GalenConfig.getConfig().readProperty(GalenProperty.GALEN_OCR_GOOGLE_VISION_KEY);
		if (key == null) {
			throw new RuntimeException("Missing property " + GalenProperty.GALEN_OCR_GOOGLE_VISION_KEY + ". See https://cloud.google.com/vision/docs/auth for more info");
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

        return postOcrImage(key, grequest);

	}

    private static GoogleModel postOcrImage(String key, GoogleRequest grequest) throws IOException {
	    String url = BASE_URL + key;
        HttpResponse response = post(url, grequest);
        int status = response.getStatusLine().getStatusCode();
        String responseText = IOUtils.toString(response.getEntity().getContent());

        if (status < 400) {
            System.out.println("\n" + responseText);
            return objectMapper.readValue(responseText, GoogleModel.class);
        } else {
            String message;
            try {
                JsonNode tree = objectMapper.readTree(responseText);
                message = tree.get("error").get("message").asText();
            } catch (Exception ex) {
               message = responseText;
            }

            throw new IOException("Response " + status + ". " + message);
        }
    }

    private static HttpResponse post(String url, Object requestObject) throws IOException {
        String json = objectMapper.writeValueAsString(requestObject);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(json));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return httpClient.execute(httpPost);
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
