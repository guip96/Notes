package pt.ipleiria.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.WebDetection;
import com.google.api.services.vision.v1.model.WebEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CloudVisionTask: Envia a photo para a CloudVision API e recebe campos, apenas são seleccionados
 * campos com certeza acima de 50%
 */
class CloudVisionTask extends AsyncTask<Object, Void, String> {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyC_0Dz1a4x6UKz-DNZTzpvrcviATJncxr8";
    private static final String TAG = "TAG";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private Bitmap bip;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;


    public CloudVisionTask(Bitmap photo, Context mContext) {
        this.bip = photo;
        this.mContext= mContext;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            VisionRequestInitializer requestInitializer =
                    new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                        /**
                         * We override this so we can inject important identifying fields into the HTTP
                         * headers. This enables use of a restricted cloud platform API key.
                         */
                        @Override
                        protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                throws IOException {
                            super.initializeVisionRequest(visionRequest);

                            String packageName = mContext.getPackageName();
                            visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                            String sig = PackageManagerUtils.getSignature(mContext.getPackageManager(), packageName);

                            visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                        }
                    };

            Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
            builder.setVisionRequestInitializer(requestInitializer);

            Vision vision = builder.build();

            BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                    new BatchAnnotateImagesRequest();
            batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                // Add the image
                Image base64EncodedImage = new Image();
                // Convert the bitmap to a JPEG
                // Just in case it's a format that Android understands but Cloud Vision
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bip.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Base64 encode the JPEG
                base64EncodedImage.encodeContent(imageBytes);
                annotateImageRequest.setImage(base64EncodedImage);

                // add the features we want
                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                    Feature labelDetection = new Feature();
                    labelDetection.setType("LABEL_DETECTION");
                    labelDetection.setMaxResults(10);
                    add(labelDetection);

                    Feature textDetection = new Feature();
                    textDetection.setType("TEXT_DETECTION");
                    textDetection.setMaxResults(10);
                    add(textDetection);

                    Feature landmarkDetection = new Feature();
                    landmarkDetection.setType("LANDMARK_DETECTION");
                    landmarkDetection.setMaxResults(10);
                    add(landmarkDetection);

                    Feature logoDetection = new Feature();
                    logoDetection.setType("LOGO_DETECTION");
                    logoDetection.setMaxResults(10);
                    add(logoDetection);

                    Feature webDetection = new Feature();
                    webDetection.setType("WEB_DETECTION");
                    webDetection.setMaxResults(10);
                    add(webDetection);
                }});

                // Add the list of one thing to the request
                add(annotateImageRequest);
            }});

            Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotateRequest.setDisableGZipContent(true);
            Log.d(TAG, "created Cloud Vision request object, sending request");

            BatchAnnotateImagesResponse response = annotateRequest.execute();
            return convertResponseToString(response);

        } catch (GoogleJsonResponseException e) {
            Log.d(TAG, "failed to make API request because " + e.getContent());
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
        return "Cloud Vision API request failed. Check logs for details.";
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";
        List<EntityAnnotation> annotations;
        AnnotateImageResponse annotateImageResponse = response.getResponses().get(0);

        WebDetection webDetection = annotateImageResponse.getWebDetection();
        if (webDetection != null) {
            List<WebEntity> webEntities = webDetection.getWebEntities();
            if (webEntities != null) {
                for (WebEntity webEntity : webEntities) {
                    if(webEntity.getScore()>=2){
                        message+=webEntity.getDescription()+"\n";
                    }
                }
            }
        }

        annotations = annotateImageResponse.getLabelAnnotations();
        if (annotations != null) {
            for (EntityAnnotation annotation : annotations) {
                if(annotation.getScore()>=0.5){
                    message+=annotation.getDescription()+"\n";
                }
            }
        }

        annotations = annotateImageResponse.getLandmarkAnnotations();
        if (annotations != null) {
            for (EntityAnnotation annotation : annotations) {
                if(annotation.getScore()>=0.5){
                    message+=annotation.getDescription()+"\n";
                }
            }
        }

        annotations = annotateImageResponse.getLogoAnnotations();
        if (annotations != null) {
            for (EntityAnnotation annotation : annotations) {
                if(annotation.getScore()>=0.5){
                    message+=annotation.getDescription()+"\n";
                }
            }
        }

        return message;
    }

}

