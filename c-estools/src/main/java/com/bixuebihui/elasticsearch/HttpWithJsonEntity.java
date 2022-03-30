package com.bixuebihui.elasticsearch;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * @author xwx
 */
public class HttpWithJsonEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";
    private String method;

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public String getMethod() {
        return method;
    }

    public static class Builder{
        String url;
        String body;
        String method = METHOD_NAME;

        public Builder withUrl(String url){
            this.url = url;
            return this;
        }
        public Builder withMethod(String method){
            this.method = method;
            return this;
        }
        public Builder withBodyEntry(String body){
            this.body = body;
            return this;
        }

        public HttpWithJsonEntity build() throws URISyntaxException, UnsupportedEncodingException {
            HttpWithJsonEntity request = new HttpWithJsonEntity();
            request.setMethod(method);
            request.setURI(new URI(this.url));
            request.setEntity(new StringEntity(this.body, ContentType.APPLICATION_JSON));
            return request;
        }
    }

    private void setMethod(String method) {
        this.method = method;
    }


}
