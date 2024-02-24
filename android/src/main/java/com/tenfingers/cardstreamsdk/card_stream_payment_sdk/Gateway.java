package com.tenfingers.cardstreamsdk.card_stream_payment_sdk;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Hijbullah Al Mahmud
 */
public class Gateway {

    protected static final int BUFFER_SIZE = 8192;

    /**
     * Transaction successful response code
     */
    @SuppressWarnings("unused")
    public static final int RC_SUCCESS = 0;

    /**
     * Transaction declined response code
     */
    @SuppressWarnings("unused")
    public static final int RC_DO_NOT_HONOR = 5;

    /**
     * Verification successful response code
     */
    @SuppressWarnings("unused")
    public static final int RC_NO_REASON_TO_DECLINE = 85;

    @SuppressWarnings("unused")
    public static final int RC_3DS_AUTHENTICATION_REQUIRED = 0x1010A;

    private static final String[] REMOVE_REQUEST_FIELDS = {
            "directUrl",
            "hostedUrl",
            "merchantAlias",
            "merchantID2",
            "merchantSecret",
            "responseCode",
            "responseMessage",
            "responseStatus",
            "signature",
            "state",
    };

    private final URL gatewayUrl;
    private final String merchantID;
    private final String merchantSecret;
    private final String merchantPwd;
    private final Proxy proxy;

    public Gateway(
            final String gatewayUrl,
            final String merchantID,
            final String merchantSecret,
            final String merchantPwd,
            final Proxy proxy
    ) {

        try {
            this.gatewayUrl = new URL(gatewayUrl);
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }

        this.merchantID = merchantID;
        this.merchantSecret = merchantSecret;
        this.merchantPwd = merchantPwd;
        this.proxy = proxy;

    }


    public Gateway(
            final String gatewayUrl,
            final String merchantID,
            final String merchantSecret,
            final String merchantPwd
    ) {
        this(gatewayUrl, merchantID, merchantSecret, merchantPwd, Proxy.NO_PROXY);
    }

    public Gateway(final String gatewayUrl, final String merchantID, final String merchantSecret) {
        this(gatewayUrl, merchantID, merchantSecret, null);
    }

    
    public Map<String, String> directRequest(
            final Map<String, String> request,
            final Map<String, String> options
    ) throws IOException {

        final URL directUrl;

        if (request.containsKey("directUrl")) {
            try {
                directUrl = new URL(request.get("directUrl"));
            } catch (final MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            directUrl = this.gatewayUrl;
        }

        final String secret;

        if (request.containsKey("merchantSecret")) {
            secret = request.get("merchantSecret");
        } else {
            secret = this.merchantSecret;
        }

        final Map<String, String> _request = this.prepareRequest(request, options);

        if (secret != null) {
            _request.put("signature", this.sign(_request, secret));
        }

        final URLConnection _connection = directUrl.openConnection(this.proxy);
        final HttpURLConnection connection = (HttpURLConnection) _connection;
        final byte[] data = buildQueryString(_request).getBytes();

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(data.length));

        final DataOutputStream out = new DataOutputStream(connection.getOutputStream());

        out.write(data, 0, data.length);
        out.flush();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        final String response = receive(connection.getInputStream(), BUFFER_SIZE);
        final Map<String, String> _response = parseQueryString(response);

        return this.verifyResponse(_response, secret);

    }

    @SuppressWarnings("unused")
    public Map<String, String> directRequest(final Map<String, String> request) throws IOException {
        return this.directRequest(request, new TreeMap<String, String>());
    }

    
    public String hostedRequest(
            final Map<String, String> request,
            final Map<String, String> options
    ) {

        final URL hostedUrl;

        if (request.containsKey("hostedUrl")) {
            try {
                hostedUrl = new URL(request.get("hostedUrl"));
            } catch (final MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            hostedUrl = this.gatewayUrl;
        }

        final String secret;

        if (request.containsKey("merchantSecret")) {
            secret = request.get("merchantSecret");
        } else {
            secret = this.merchantSecret;
        }

        final Map<String, String> _request = this.prepareRequest(request, options);

        if (secret != null) {
            _request.put("signature", this.sign(_request, secret, true));
        }

        final StringBuilder form = new StringBuilder("<form method=\"post\" ");

        if (options.containsKey("formAttrs")) {
            form.append(options.get("formAttrs"));
        }

        form.append(" action=\"");
        form.append(TextUtils.htmlEncode(hostedUrl.toString()));
        form.append("\">\n");

        for (final Map.Entry<String, String> entry : _request.entrySet()) {
            form.append(this.fieldToHtml(entry.getKey(), entry.getValue()));
        }

        if (options.containsKey("submitHtml")) {
            form.append("<button type=\"submit\" ");
        } else {
            form.append("<input ");
        }

        if (options.containsKey("submitAttrs")) {
            form.append(options.get("submitAttrs"));
        }

        if (options.containsKey("submitImage")) {
            form.append(" type=\"image\" src=\"");
            form.append(TextUtils.htmlEncode(options.get("submitImage")));
            form.append("\">\n");
        } else if (options.containsKey("submitHtml")) {
            form.append(">");
            form.append(options.get("submitHtml"));
            form.append("</button>\n");
        } else if (options.containsKey("submitText")) {
            form.append(" type=\"submit\" value=\"");
            form.append(TextUtils.htmlEncode(options.get("submitText")));
            form.append("\">\n");
        } else {
            form.append(" type=\"submit\" value=\"Pay Now\">\n");
        }

        form.append("</form>\n");

        return form.toString();

    }

    @SuppressWarnings("unused")
    public String hostedRequest(final Map<String, String> request) throws IOException {
        return this.hostedRequest(request, new TreeMap<String, String>());
    }

    
    public Map<String, String> prepareRequest(
            final Map<String, String> request,
            @SuppressWarnings("unused")
            final Map<String, String> options
    ) {

        if (!request.containsKey("action")) {
            throw new IllegalArgumentException("Request must contain an 'action'.");
        }

        final Map<String, String> _request = new TreeMap<>(request);

        if (!_request.containsKey("merchantID")) {
            _request.put("merchantID", this.merchantID);
        }

        if (!_request.containsKey("merchantPwd") && this.merchantPwd != null) {
            _request.put("merchantPwd", this.merchantPwd);
        }

        if (_request.get("merchantID") == null) {
            throw new IllegalArgumentException("Merchant ID or Alias must be provided.");
        }

        for (final String name : REMOVE_REQUEST_FIELDS) {
            _request.remove(name);
        }

        return _request;

    }

    @SuppressWarnings("unused")
    public Map<String, String> prepareRequest(final Map<String, String> request) {
        return this.prepareRequest(request, new TreeMap<String, String>());
    }

   
    public Map<String, String> verifyResponse(
            final Map<String, String> response,
            String secret
    ) {

        if (!response.containsKey("responseCode")) {
            throw new IllegalArgumentException("Invalid response from Payment Gateway");
        }

        if (secret == null) {
            secret = this.merchantSecret;
        }

        final Map<String, String> _response = new TreeMap<>(response);

        String signature = null, partial = null;

        if (_response.containsKey("signature")) {

            signature = _response.remove("signature");

            if (signature != null) {

                int pipe = signature.indexOf('|');

                if (pipe != -1) {
                    partial = signature.substring(pipe + 1);
                    signature = signature.substring(0, pipe);
                }

            }

        }

        if (secret == null && signature != null) {
            // Signature present when not expected (Gateway has a secret but we don't)
            throw new IllegalArgumentException("Incorrectly signed response from Payment Gateway (1)");
        }

        if (secret != null && signature == null) {
            // Signature missing when one expected (we have a secret but the Gateway doesn't)
            throw new IllegalArgumentException("Incorrectly signed response from Payment Gateway (2)");
        }

        if (secret != null && !signature.equals(sign(_response, secret, partial))) {
            // Signature mismatch
            throw new IllegalArgumentException("Incorrectly signed response from Payment Gateway");
        }

        return _response;

    }

    @SuppressWarnings("unused")
    public Map<String, String> verifyResponse(final Map<String, String> response) {
        return this.verifyResponse(response, null);
    }

    public String sign(final Map<String, String> data, String secret, Object partial) {

        if (secret == null) {
            secret = this.merchantSecret;
        }

        final Map<String, String> _data = new TreeMap<>(data);

        if (partial != null) {

            if (partial instanceof String) {
                partial = Arrays.asList(((String) partial).split(","));
            }

            if (partial instanceof Collection) {
                for (final String name : _data.keySet()) {
                    if (!((Collection) partial).contains(name)) {
                        _data.remove(name);
                    }
                }
            }

            final List<String> _partial = new ArrayList<>(_data.keySet());

            Collections.sort(_partial);
            partial = TextUtils.join(",", _partial);

        }

        try {

            final MessageDigest digest = MessageDigest.getInstance("SHA-512");
            final String message = buildQueryString(_data);

            digest.update((message + secret).getBytes());

            final String signature = toHexadecimal(digest.digest());

            if (partial != null) {
                return signature + "|" + partial;
            }

            return signature;

        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("unused")
    public String sign(final Map<String, String> data, final String secret) {
        return this.sign(data, secret, null);
    }

    @SuppressWarnings("unused")
    public String sign(final Map<String, String> data) {
        return this.sign(data, null);
    }

    
    @NonNull
    public String fieldToHtml(final String name, final String value) {

        if (value == null || value.isEmpty()) {
            return "";
        }

        final String _name = TextUtils.htmlEncode(name);
        final String _value = TextUtils.htmlEncode(value);

        return MessageFormat.format("<input type=\"hidden\" name=\"{0}\" value=\"{1}\" />\n", _name, _value);

    }

    @NonNull
    static String buildQueryString(final Map<String, String> data) {
        try {

            final StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> e : data.entrySet()) {

                if (query.length() > 0) {
                    query.append('&');
                }

                query.append(URLEncoder.encode(e.getKey(), "UTF-8"));
                query.append("=");
                query.append(URLEncoder.encode(e.getValue(), "UTF-8"));

            }

            return query.toString()
                    .replaceAll("(?i)%0D%0A|%0A%0D|%0D", "%0A")
                    .replaceAll("\\*", "%2A");

        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    static Map<String, String> parseQueryString(final String query) {
        try {

            // based on http://stackoverflow.com/a/13592567

            final Map<String, String> data = new LinkedHashMap<>();

            for (final String pair : query.split("&")) {

                final int index = pair.indexOf("=");

                final String parameter, value;

                if (index > 0) {
                    parameter = URLDecoder.decode(pair.substring(0, index), "UTF-8");
                    value = URLDecoder.decode(pair.substring(index + 1), "UTF-8");
                } else {
                    parameter = URLDecoder.decode(pair, "UTF-8");
                    value = null;
                }

                data.put(parameter, value);

            }

            return data;

        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    static String receive(final InputStream in, final int bufferSize) throws IOException {

        final InputStreamReader reader = new InputStreamReader(in);
        final StringBuilder content = new StringBuilder();
        final char[] buffer = new char[bufferSize];

        int length = 0;

        while (length != -1) {

            if (length > 0) {
                content.append(buffer, 0, length);
            }

            length = reader.read(buffer, 0, bufferSize);

        }

        return content.toString();

    }

    protected static final char[] hexadecimal = "0123456789abcdef".toCharArray();

    @NonNull
    static String toHexadecimal(final byte[] data) {

        // based on http://stackoverflow.com/a/9855338

        final char[] s = new char[data.length * 2];

        for (int i = 0; i < data.length; i++) {

            int b = data[i] & 0xFF; // isolate octet

            s[i * 2] = hexadecimal[b >>> 4]; // high four bits
            s[i * 2 + 1] = hexadecimal[b & 0x0F]; // low four bits

        }

        return new String(s);

    }

}