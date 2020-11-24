package com.yanwu.spring.cloud.common.pojo;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import javax.annotation.concurrent.NotThreadSafe;
import java.net.URI;

/**
 * @author Baofeng Xu
 * @date 2020/11/24 10:15.
 * <p>
 * description:
 */
@NotThreadSafe
public class HttpDelete extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDelete() {
        super();
    }

    public HttpDelete(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDelete(final String uri) {
        super();
        setURI(URI.create(uri));
    }

}
