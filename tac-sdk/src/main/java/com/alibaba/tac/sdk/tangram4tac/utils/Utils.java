package com.alibaba.tac.sdk.tangram4tac.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by longerian on 2017/11/14.
 */
public class Utils {

    public static String format (
            final List<Pair<String, String>> parameters,
            final String encoding) {
        final StringBuilder result = new StringBuilder();
        for (final Pair<String, String> parameter : parameters) {
            final String encodedName = encode(parameter.first, encoding);
            final String value = parameter.second;
            final String encodedValue = value != null ? encode(value, encoding) : "";
            if (result.length() > 0)
                result.append(PARAMETER_SEPARATOR);
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    public static List <Pair<String, String>> parse (final URI uri, final String encoding) {
        List <Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        final String query = uri.getRawQuery();
        if (query != null && query.length() > 0) {
            parse(result, new Scanner(query), encoding);
        }
        return result;
    }

    private static final String PARAMETER_SEPARATOR = "&";

    private static final String NAME_VALUE_SEPARATOR = "=";

    public static void parse (
            final List <Pair<String, String>> parameters,
            final Scanner scanner,
            final String encoding) {
        scanner.useDelimiter(PARAMETER_SEPARATOR);
        while (scanner.hasNext()) {
            String nameValue = scanner.next();
            if (nameValue == null || nameValue.length() == 0) {
                continue;
            }
            int indexOfEq = nameValue.indexOf(NAME_VALUE_SEPARATOR);
            if (indexOfEq == -1) {
                continue;
            }
            String name = nameValue.substring(0, indexOfEq);
            String value = nameValue.substring(indexOfEq + 1);
            if ((value == null || value.length() == 0) || (name == null || name.length() == 0)) {
                continue;
            }
            name = decode(name, encoding);
            value = decode(value, encoding);
            parameters.add(new Pair<String, String>(name, value));
        }
    }

    public static URI createURI(
            final String scheme,
            final String host,
            int port,
            final String path,
            final String query,
            final String fragment) throws URISyntaxException {

        StringBuilder buffer = new StringBuilder();
        if (host != null) {
            if (scheme != null) {
                buffer.append(scheme);
                buffer.append("://");
            } else {
                buffer.append("//");
            }
            buffer.append(host);
            if (port > 0) {
                buffer.append(':');
                buffer.append(port);
            }
        }
        if (path == null || !path.startsWith("/")) {
            buffer.append('/');
        }
        if (path != null) {
            buffer.append(path);
        }
        if (query != null) {
            buffer.append('?');
            buffer.append(query);
        }
        if (fragment != null) {
            buffer.append('#');
            buffer.append(fragment);
        }
        return new URI(buffer.toString());
    }

    private static String decode (final String content, final String encoding) {
        try {
            return URLDecoder.decode(content,
                    encoding != null ? encoding : "UTF-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    private static String encode (final String content, final String encoding) {
        try {
            return URLEncoder.encode(content,
                    encoding != null ? encoding : "UTF-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

}
