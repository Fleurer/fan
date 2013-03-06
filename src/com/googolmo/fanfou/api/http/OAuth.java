package com.googolmo.fanfou.api.http;


import com.googolmo.fanfou.utils.Utils;
import org.apache.http.NameValuePair;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: googolmo
 * Date: 12-9-8
 * Time: 上午1:54
 */
public class OAuth {
    private String consumerKey;
    private String consumerSecret;

    public OAuth(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getOAuthSignature(String httpMethod,
                                    String url,
                                    List<NameValuePair> params,
                                    Token token) {

        long timestamp = System.currentTimeMillis() / 1000;
        long nonce = timestamp + Utils.getRandom();
        return getOAuthSignature(httpMethod, url, params, String.valueOf(timestamp), String.valueOf(nonce), token);


    }

    public String getOAuthSignature(String httpMethod,
                                    String url,
                                    List<NameValuePair> params,
                                    String timestamp,
                                    String nonce,
                                    Token token) {
        if (params == null) {
            params = new ArrayList<NameValuePair>();
        }

        List<Parameter> oauthHeaderParams = new ArrayList<Parameter>();
        oauthHeaderParams.add(new Parameter("oauth_consumer_key", consumerKey));
        oauthHeaderParams.add(new Parameter("oauth_signature_method", "HMAC-SHA1"));
        oauthHeaderParams.add(new Parameter("oauth_timestamp", timestamp));
        oauthHeaderParams.add(new Parameter("oauth_nonce", nonce));
        oauthHeaderParams.add(new Parameter("oauth_version", "1.0"));
        if (token != null) {
            oauthHeaderParams.add(new Parameter("oauth_token", token.getToken()));
        }
        List<Parameter> signBaseParams = new ArrayList<Parameter>(oauthHeaderParams.size() + params.size());
        signBaseParams.addAll(oauthHeaderParams);
        signBaseParams.addAll(toParamList(params));
        parseGetParameters(url, signBaseParams);

        StringBuilder base_elems = new StringBuilder();
        base_elems.append(httpMethod).append("&").append(encode(constructRequestURL(url))).append("&");
        base_elems.append(encode(normalizeRequestParameters(signBaseParams)));

        String signatore = generateSignature(base_elems.toString(), token);

        oauthHeaderParams.add(new Parameter("oauth_signature", signatore));
        return "OAuth " + encodeParameters(oauthHeaderParams, ",", true);

    }


    private void parseGetParameters(String url, List<Parameter> signBaseParams) {
        int queryStart = url.indexOf("?");
        if (queryStart != -1) {
            String[] queryStrs = url.substring(queryStart + 1).split("&");
            try{
                for (String query : queryStrs) {
                    String[] split = query.split("=");
                    if (split.length == 2) {
                        signBaseParams.add(new Parameter(URLDecoder.decode(split[0],
                                "UTF-8"), URLDecoder.decode(split[1], "UTF-8")));
                    } else {
                        signBaseParams.add(new Parameter(URLDecoder.decode(split[0],
                                "UTF-8"), ""));
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data the data to be signed
     * @return signature
     * @see <a href="http://oauth.net/core/1.0/#rfc.section.9.2.1">OAuth Core - 9.2.1.  Generating Signature</a>
     */
    /*package*/ String generateSignature(String data, Token token) {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec;
            if (null == token) {
                String oauthSignature = encode(consumerSecret) + "&";
                spec = new SecretKeySpec(oauthSignature.getBytes(), "HmacSHA1");
            } else {
                if (null == token.getSecretKeySpec()) {
                    String oauthSignature = encode(consumerSecret) + "&" + encode(token.getTokenSecret());
                    spec = new SecretKeySpec(oauthSignature.getBytes(), "HmacSHA1");
                    token.setSecretKeySpec(spec);
                }
                spec = token.getSecretKeySpec();
            }
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
        }
        return BASE64Encoder.encode(byteHMAC);
    }

    public List<Parameter> toParamList(List<NameValuePair> params) {
        List<Parameter> parameters = new ArrayList<Parameter>(params.size());
        for (NameValuePair pair : params) {
            parameters.add(new Parameter(pair.getName(), pair.getValue()));
        }
        return parameters;
    }

    public String normalizeRequestParameters(List<Parameter> params) {
        Collections.sort(params);
        return encodeParameters(params);
    }

    /**
     * @param params parameters to be enocded and concatenated
     * @return eoncoded string
     * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
     * @see <a href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space encoding - OAuth | Google Groups</a>
     */
    public static String encodeParameters(List<Parameter> params) {
        return encodeParameters(params, "&", false);
    }

    public static String encodeParameters(List<Parameter> params, String splitter, boolean quot) {
        StringBuffer buf = new StringBuffer();
        for (Parameter param : params) {
            if (buf.length() != 0) {
                if (quot) {
                    buf.append("\"");
                }
                buf.append(splitter);
            }
            buf.append(encode(param.key)).append("=");
            if (quot) {
                buf.append("\"");
            }
            buf.append(
                    encode(param.value));
        }
        if (buf.length() != 0) {
            if (quot) {
                buf.append("\"");
            }
        }
        return buf.toString();
    }

    /**
     * @param value string to be encoded
     * @return encoded string
     * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
     * @see <a href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space encoding - OAuth | Google Groups</a>
     * @see <a href="http://tools.ietf.org/html/rfc3986#section-2.1">RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax - 2.1. Percent-Encoding</a>
     */
    public static String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }

    /**
     * The Signature Base String includes the request absolute URL, tying the signature to a specific endpoint. The URL used in the Signature Base String MUST include the scheme, authority, and path, and MUST exclude the query and fragment as defined by [RFC3986] section 3.<br>
     * If the absolute request URL is not available to the Service Provider (it is always available to the Consumer), it can be constructed by combining the scheme being used, the HTTP Host header, and the relative HTTP request URL. If the Host header is not available, the Service Provider SHOULD use the host name communicated to the Consumer in the documentation or other means.<br>
     * The Service Provider SHOULD document the form of URL used in the Signature Base String to avoid ambiguity due to URL normalization. Unless specified, URL scheme and authority MUST be lowercase and include the port number; http default port 80 and https default port 443 MUST be excluded.<br>
     * <br>
     * For example, the request:<br>
     * HTTP://Example.com:80/resource?id=123<br>
     * Is included in the Signature Base String as:<br>
     * http://example.com/resource
     *
     * @param url the url to be normalized
     * @return the Signature Base String
     * @see <a href="http://oauth.net/core/1.0#rfc.section.9.1.2">OAuth Core - 9.1.2.  Construct Request URL</a>
     */
    public static String constructRequestURL(String url) {
        int index = url.indexOf("?");
        if (-1 != index) {
            url = url.substring(0, index);
        }
        int slashIndex = url.indexOf("/", 8);
        String baseUrl = url.substring(0, slashIndex).toLowerCase();
        int colonIndex = baseUrl.indexOf(":", 8);
        if (-1 != colonIndex) {
            if (baseUrl.startsWith("http://") && baseUrl.endsWith(":80")) {
                baseUrl = baseUrl.substring(0, colonIndex);
            } else if (baseUrl.startsWith("https://") && baseUrl.endsWith(":433")) {
                baseUrl = baseUrl.substring(0, colonIndex);
            }
        }
        url = baseUrl + url.substring(slashIndex);
        return url;
    }


    public class Parameter implements Comparable{
        private String key;
        private String value;

        private Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean contain(String key) {
            return this.key.equalsIgnoreCase(key);
        }

        @Override
        public int compareTo(Object o) {
            int compared;
            Parameter that = (Parameter) o;
            compared = key.compareTo(that.key);
            if (0 == compared) {
                compared = value.compareTo(that.value);
            }
            return compared;
        }
    }


}
