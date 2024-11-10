package io.github.openfacade.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UrlBuilder {
    StringBuilder url;
    List<Param> params = new LinkedList<>();
    public UrlBuilder(String url) {
        this.url = new StringBuilder(url);
    }

    public UrlBuilder addParameter(String key, String value) {
        params.add(new Param(key, value));
        return this;
    }

    public String build() {
        if (params.size() == 0) {
            return url.toString();
        }

        url.append("?");
        Iterator<Param> iterator = params.iterator();
        while (true) {
            Param param = iterator.next();
            url.append(encode(param.key));
            url.append('=');
            url.append(encode(param.value));

            boolean reachEnd = iterator.hasNext();
            if (!reachEnd) {
                break;
            }
            url.append("&");
        }
        return url.toString();
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("failed to encode url", e);
        }
    }

    private static class Param {
        String key;
        String value;

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
