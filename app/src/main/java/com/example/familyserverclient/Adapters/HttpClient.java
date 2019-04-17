package com.example.familyserverclient.Adapters;

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
            connection.setConnectTimeout(2000);
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

    public String getRequest(URL url, String auth_token)
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setRequestProperty("Authorization", auth_token);
            connection.setRequestMethod("GET");
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
