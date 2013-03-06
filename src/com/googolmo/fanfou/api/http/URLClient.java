package com.googolmo.fanfou.api.http;

import com.googolmo.fanfou.Constants;
import org.apache.http.NameValuePair;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午7:36
 */
public class URLClient {

    public URLClient() {
    }

    public Response fetch(String url, Method method, List<NameValuePair> params, List<NameValuePair> headers) {
        int code = 200;
        try {
            URL aUrl = new URL(url);
            HttpURLConnection connection;
//            if (aUrl.getProtocol().equalsIgnoreCase("https")) {
//                connection = (HttpsURLConnection) aUrl.openConnection();
//            } else {
                connection = (HttpURLConnection) aUrl.openConnection();
//            }
            try {
                connection.setDoInput(true);
                if (method.name().equals("POST") || method.name().equals("PUT")) {
                    connection.setDoOutput(true);
                    if (params != null) {
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getQuery(params));
                    }
                }

                connection.setConnectTimeout(Constants.URL_CONNECTION_TIMEOUT);
                connection.setReadTimeout(Constants.URL_READ_TIMEOUT);
                connection.setRequestMethod(method.name());

                connection.setRequestProperty("Accept-Encoding", "gzip");
                connection.setRequestProperty("User-Agent", Constants.URL_USER_AGENT);
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Connection", "keep-alive");
                //TODO DNS解析

                //设置Headers
                if (headers != null) {
                    for (NameValuePair pair : headers) {
                        connection.setRequestProperty(pair.getName(), pair.getValue());
                    }
                }

                connection.connect();

                code = connection.getResponseCode();
                boolean isGzip = connection.getContentEncoding().equalsIgnoreCase("gzip");
                if (code > 300) {
                    return new Response(readStream(connection.getErrorStream(), isGzip), code, getMessageByCode(code));
                } else {
                    return new Response(readStream(connection.getInputStream(), isGzip), code, connection.getResponseMessage());
                }

            } finally {
                connection.disconnect();
            }



        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new Response("", 400, "Malformed URL: " + url);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response("", 999, e.getMessage());
        }
    }


    public String getQuery(List<NameValuePair> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (NameValuePair pair : params) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String readStream(InputStream inputStream, boolean isGZIP) throws IOException {
        StringWriter stringWriter = new StringWriter();

        char[] buf = new char[1024];
        int l = 0;
        if (null != inputStream && isGZIP) {
            inputStream = new GZIPInputStream(inputStream);
        }

        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
        while ((l = reader.read(buf))  > 0) {
            stringWriter.write(buf, 0 , 1);
        }
        stringWriter.flush();
        stringWriter.close();
        return stringWriter.getBuffer().toString();
    }

    /**
     * Returns message for code
     *
     * @param code code
     * @return Message
     */
    private String getMessageByCode(int code) {
        switch (code) {
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 500:
                return "Internal Server Error";
            default:
                return "Unknown";
        }
    }

}
