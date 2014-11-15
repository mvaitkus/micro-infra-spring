package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive;

import groovy.transform.TypeChecked;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;

/**
 * Default implementation of header setting for requests
 *
 * @param < T > - original class to be returned once header setting has finished
 */
@TypeChecked
public class WithHeaders<T> implements HeadersSetting<T>, HeadersHaving<T> {
    public WithHeaders(T parent, Map<String, String> params) {
        this.params = params;
        this.parent = parent;
    }

    @Override
    public WithHeaders accept(List<MediaType> acceptableMediaTypes) {
        httpHeaders.setAccept(acceptableMediaTypes);
        updateHeaderParams();
        return this;
    }

    @Override
    public WithHeaders accept(MediaType... acceptableMediaTypes) {
        return accept(Arrays.asList(acceptableMediaTypes));
    }

    @Override
    public WithHeaders cacheControl(String cacheControl) {
        httpHeaders.setCacheControl(cacheControl);
        updateHeaderParams();
        return this;
    }

    @Override
    public WithHeaders contentType(MediaType mediaType) {
        httpHeaders.setContentType(mediaType);
        updateHeaderParams();
        return this;
    }

    @Override
    public HeadersSetting<ResponseReceiving> contentType(String contentType) {
        httpHeaders.add(CONTENT_TYPE_HEADER_NAME, contentType);
        updateHeaderParams();
        return this;
    }

    @Override
    public HeadersSetting<T> contentTypeJson() {
        httpHeaders.setContentType(APPLICATION_JSON);
        updateHeaderParams();
        return this;
    }

    @Override
    public HeadersSetting<T> contentTypeXml() {
        httpHeaders.setContentType(APPLICATION_XML);
        updateHeaderParams();
        return this;
    }

    @Override
    public WithHeaders expires(long expires) {
        httpHeaders.setExpires(expires);
        updateHeaderParams();
        return this;
    }

    @Override
    public WithHeaders lastModified(long lastModified) {
        httpHeaders.setLastModified(lastModified);
        updateHeaderParams();
        return this;
    }

    @Override
    public WithHeaders location(URI location) {
        httpHeaders.setLocation(location);
        updateHeaderParams();
        return this;
    }

    @Override
    public WithHeaders header(String headerName, String headerValue) {
        httpHeaders.add(headerName, headerValue);
        updateHeaderParams();
        return this;
    }

    @Override
    public WithHeaders headers(Map<String, String> values) {
        httpHeaders.setAll(values);
        updateHeaderParams();
        return this;
    }

    @Override
    public HeadersSetting<T> headers(HttpHeaders httpHeaders) {
        params.headers = httpHeaders;
        return this;
    }

    private void updateHeaderParams() {
        params.headers = httpHeaders;
    }

    @Override
    public T andExecuteFor() {
        return parent;
    }

    @Override
    public HeadersSetting<ResponseReceiving> withHeaders() {
        return this;
    }

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final Map params;
    private final T parent;
}