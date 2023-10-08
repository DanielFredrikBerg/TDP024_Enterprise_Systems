package se.liu.ida.tdp024.account.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;

public class HTTPHelperImpl implements HTTPHelper {

    private AccountLogger accountLogger = new AccountLoggerImpl();

    @Override
    public String get(String endpoint, String... parameters) {

        String urlToRead = createURL(endpoint, parameters);

        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            accountLogger.log(e);
        }
        return result;

    }

    @Override
    public String postJSON(String endpoint, String[] queryParameters, String[] dataParameters) {


        String urlToRead = createURL(endpoint, queryParameters);
        String dataPayload = createJSONPayload(dataParameters);

        try {

            URL url = new URL(urlToRead);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(60000);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            writer.write(dataPayload);
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                return builder.toString();

            } else {


                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                return builder.toString();

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    private String createURL(String endpoint, String... parameters) {

        StringBuilder urlBuilder = new StringBuilder(endpoint);
        for (int i = 0; i < (parameters.length - 1); i += 2) {
            try {
                String parameterName = URLEncoder.encode(parameters[i], "UTF-8");
                String parameterValue = URLEncoder.encode(parameters[i + 1], "UTF-8");

                if (i == 0) {
                    urlBuilder.append("?");
                } else {
                    urlBuilder.append("&");
                }

                urlBuilder.append(parameterName).append("=").append(parameterValue);
            } catch (Exception e) {
                accountLogger.log(e);
            }

        }

        return urlBuilder.toString();

    }

    private String createJSONPayload(String[] dataParameters) {

        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("{");
        for (int i = 0; i < (dataParameters.length - 1); i += 2) {

            try {
                Long longParameters = Long.parseLong(dataParameters[i + 1]);
                dataBuilder.append("\"").append(dataParameters[i]).append("\":").append(longParameters).append(",");
            } catch (NumberFormatException e) {
                dataBuilder.append("\"").append(dataParameters[i]).append("\":\"").append(dataParameters[i + 1]).append("\",");
            }

        }

        if (dataBuilder.length() > 1) {
            dataBuilder.deleteCharAt(dataBuilder.length() - 1);
        }

        dataBuilder.append("}");
        
        String safeJSON = dataBuilder.toString().replaceAll("\\\n", "\\\\n");
        return safeJSON;

    }
}
