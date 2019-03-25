package com.example.familyserverclient.Models;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient
{
    public String postRequest(URL url, String requestBody)
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestBody);
            writer.flush();
            writer.close();
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                InputStream responseBody = connection.getInputStream();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = responseBody.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, length);
                }
                String responseBodyData = baos.toString();
                return responseBodyData;
            }
        }
        catch (Exception e)
        {
            Log.e("HttpClient", e.getMessage(), e);
        }
        return null;
    }
}
