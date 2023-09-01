package com.sumit.dalleimageedit;

import android.app.Activity;
import android.util.Log;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DalleImageEdit extends AndroidNonvisibleComponent implements Component {
    private final String TAG = "DalleImageEdit";
    private final Activity activity;
    private String key = "";

    public DalleImageEdit(ComponentContainer container) {
        super(container.$form());
        this.activity = container.$context();
    }

    @SimpleEvent
    public void ErrorOccurred(String error) {
        EventDispatcher.dispatchEvent(this, "ErrorOccurred", error);
    }

    @SimpleProperty
    @DesignerProperty(editorType = "string")
    public void APIKey(String key) {
        this.key = key;
    }

    @SimpleEvent
    public void GotResponse(int responseCode, String response) {
        EventDispatcher.dispatchEvent(this, "GotResponse", responseCode, response);
    }

    @SimpleFunction
    public void CreateImageEdit(String image, String mask, String prompt, int total) {
        if (key.isEmpty()) {
            ErrorOccurred("API key is empty");
            return;
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://api.openai.com/v1/images/edits";
                    String crlf = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";

                    // Setup connection
                    URL apiEndpoint = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiEndpoint.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Bearer " + key);
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    // Create multipart/form-data request
                    DataOutputStream request = new DataOutputStream(connection.getOutputStream());
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + image + "\"" + crlf);
                    request.writeBytes("Content-Type: image/png" + crlf);
                    request.writeBytes(crlf);
                    request.write(Files.readAllBytes(Paths.get(image)));
                    request.writeBytes(crlf);
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"mask\";filename=\"" + mask + "\"" + crlf);
                    request.writeBytes("Content-Type: image/png" + crlf);
                    request.writeBytes(crlf);
                    request.write(Files.readAllBytes(Paths.get(mask)));
                    request.writeBytes(crlf);
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"prompt\"" + crlf);
                    request.writeBytes(crlf);
                    request.writeBytes(crlf + prompt + crlf);
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"n\"" + crlf);
                    request.writeBytes(crlf);
                    request.writeBytes(String.valueOf(total) + crlf);
                    request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

                    // Send request
                    request.flush();
                    request.close();

                    // Get response code
                    int responseCode = connection.getResponseCode();

                    InputStream inputStream = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();

                    // Read response
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    fireEventOnUiThread("GotResponse", new Object[]{responseCode, response.toString()});
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                    fireEventOnUiThread("ErrorOccurred", new Object[]{e.getMessage()});
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    @SimpleFunction
    public void CreateImageVariation(String image, int total) {
        if (key.isEmpty()) {
            ErrorOccurred("API key is empty");
            return;
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://api.openai.com/v1/images/variations";
                    String crlf = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";

                    // Setup connection
                    URL apiEndpoint = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiEndpoint.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Bearer " + key);
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    // Create multipart/form-data request
                    DataOutputStream request = new DataOutputStream(connection.getOutputStream());
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + image + "\"" + crlf);
                    request.writeBytes("Content-Type: image/png" + crlf);
                    request.writeBytes(crlf);
                    request.write(Files.readAllBytes(Paths.get(image)));
                    request.writeBytes(crlf);
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"n\"" + crlf);
                    request.writeBytes(crlf);
                    request.writeBytes(String.valueOf(total) + crlf);
                    request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

                    // Send request
                    request.flush();
                    request.close();

                    // Get response code
                    int responseCode = connection.getResponseCode();

                    InputStream inputStream = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();

                    // Read response
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    fireEventOnUiThread("GotResponse", new Object[]{responseCode, response.toString()});
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                    fireEventOnUiThread("ErrorOccurred", new Object[]{e.getMessage()});
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    void fireEventOnUiThread(String name, Object[] args) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventDispatcher.dispatchEvent(DalleImageEdit.this, name, args);
            }
        });
    }
}