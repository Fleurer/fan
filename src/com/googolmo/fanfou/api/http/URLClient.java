package com.googolmo.fanfou.api.http;

import com.googolmo.fanfou.Constants;
import com.googolmo.fanfou.utils.NLog;
import org.apache.http.NameValuePair;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedOutputStream;
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
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * User: GoogolMo
 * Date: 13-3-6
 * Time: 下午7:36
 */
public class URLClient {
    private static final String TAG = URLClient.class.getName();

    public static final String BOUNDARYSTR = "gc0p4Jq0M2Yt08jU534c0p";
    private static final String BOUNDARY = "--" + BOUNDARYSTR + "\r\n";

    public URLClient() {
    }

    public Response fetch(String url, Method method, List<NameValuePair> params, List<NameValuePair> headers) {
        int code = 200;
        try {
            URL aUrl = new URL(url);
            HttpURLConnection connection;
                connection = (HttpURLConnection) aUrl.openConnection();
            try {

                connection.setConnectTimeout(Constants.URL_CONNECTION_TIMEOUT);
                connection.setReadTimeout(Constants.URL_READ_TIMEOUT);
                connection.setRequestMethod(method.name());

                connection.setRequestProperty("Accept-Encoding", "gzip");
                connection.setRequestProperty("User-Agent", Constants.URL_USER_AGENT);
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                connection.setRequestProperty("Connection", "keep-alive");
                //TODO DNS解析
                //设置Headers
                if (headers != null) {
                    for (NameValuePair pair : headers) {
                        connection.setRequestProperty(pair.getName(), pair.getValue());
                    }
                }

                connection.setDoInput(true);
                if (method.name().equals("POST") || method.name().equals("PUT")) {

                    connection.setDoOutput(true);
                    if (params != null) {
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getQuery(params));
                        writer.close();
                        os.close();
                    }

                }

                connection.connect();

                code = connection.getResponseCode();
                boolean isGzip = false;
                if (connection.getContentEncoding() != null) {
                    isGzip = connection.getContentEncoding().equalsIgnoreCase("gzip");
                }

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

    /**
     * 发送文件到服务器
     * @param url
     * @param method
     * @param params
     * @param headers
     * @param parameter
     * @return
     */
    public Response fetch(String url, Method method, List<NameValuePair> params, List<NameValuePair> headers, MultipartParameter parameter){
        int code = 200;
        try{
            URL aUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) aUrl.openConnection();
            try{
//                connection.setUseCaches(false);
                connection.setConnectTimeout(Constants.URL_CONNECTION_TIMEOUT);
                connection.setReadTimeout(Constants.URL_READ_TIMEOUT);
                connection.setRequestMethod(method.name());

//                connection.setRequestProperty("Accept-Encoding", "gzip");
                connection.setRequestProperty("User-Agent", Constants.URL_USER_AGENT);
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Connection", "keep-alive");

                //设置Headers
                if (headers != null) {
                    for (NameValuePair pair : headers) {
                        connection.setRequestProperty(pair.getName(), pair.getValue());
                    }
                }

                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARYSTR);

                StringBuilder formDataBuilder = new StringBuilder();

                for (NameValuePair pair : params) {
                    formDataBuilder.append(BOUNDARY);
                    formDataBuilder.append("Content-Disposition:form-data;name=\"");
                    formDataBuilder.append(pair.getName());
                    formDataBuilder.append("\"\r\n\r\n");
                    formDataBuilder.append(pair.getValue());
                    formDataBuilder.append("\r\n");
                }


                NLog.d(TAG, formDataBuilder.toString());


                StringBuilder formDataBuffer = new StringBuilder();
//                for (MultipartParameter parameter : parameters) {

                    formDataBuffer.append(BOUNDARY)
                            .append("Content-Disposition:form-data;name=\"")
                            .append(parameter.getName())
                            .append("\";filename=\"")
                            .append(parameter.getName() + "12")
                            .append(".png")
                            .append("\"\r\n")
                            .append("Content-Type:")
                            .append("image/").append(parameter.getContentType())
//                            .append("\r\n")
//                            .append("Content-Transfer-Encoding: binary")
//                            + parameter.getContentType()
                            .append("\r\n\r\n");

                NLog.d(TAG, formDataBuffer.toString());
                byte[] endData = ("\r\n--" + BOUNDARYSTR + "--\r\n").getBytes();

                NLog.d(TAG, Arrays.toString(endData));
                byte[] data = parameter.getContent();
//                byte[] data = BASE64Encoder.encode(parameter.getContent()).getBytes();
                connection.setRequestProperty("Content-Length", String.valueOf(formDataBuilder.toString().getBytes().length
                        + formDataBuffer.toString().getBytes().length + data.length + endData.length));
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();



                OutputStream outputStream = connection.getOutputStream();

                BufferedOutputStream out = new BufferedOutputStream(outputStream);
                out.write(formDataBuilder.toString().getBytes());
                out.write(formDataBuffer.toString().getBytes());
                out.write(data);
//                outputStream.write(endData);

                out.write(endData);
                out.flush();
                out.close();

                outputStream.close();


                boolean isGzip = false;
                if (connection.getContentEncoding() != null) {
                    isGzip = connection.getContentEncoding().equalsIgnoreCase("gzip");
                }
                code = connection.getResponseCode();
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
            stringWriter.write(buf, 0 , l);
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
