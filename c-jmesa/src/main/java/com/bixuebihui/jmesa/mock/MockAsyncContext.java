package com.bixuebihui.jmesa.mock;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of the {@link AsyncContext} interface.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class MockAsyncContext implements AsyncContext {

    private final HttpServletRequest request;


    private final HttpServletResponse response;

    private final List<AsyncListener> listeners = new ArrayList<>();
    private final List<Runnable> dispatchHandlers = new ArrayList<>();
    private String dispatchedPath;

    /**
     * 10 seconds is Tomcat's default
     */
    private long timeout = 10 * 1000L;


    public MockAsyncContext(ServletRequest request, ServletResponse response) {
        this.request = (HttpServletRequest) request;
        this.response = (HttpServletResponse) response;
    }


    public void addDispatchHandler(Runnable handler) {
        Assert.notNull(handler, "Dispatch handler must not be null");
        synchronized (this) {
            if (this.dispatchedPath == null) {
                this.dispatchHandlers.add(handler);
            } else {
                handler.run();
            }
        }
    }

    @Override
    public ServletRequest getRequest() {
        return this.request;
    }

    @Override

    public ServletResponse getResponse() {
        return this.response;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return (this.request instanceof SimpleHttpServletRequest && this.response instanceof SimpleHttpServletRequest);
    }

    @Override
    public void dispatch() {
        dispatch(this.request.getRequestURI());
    }

    @Override
    public void dispatch(String path) {
        dispatch(null, path);
    }

    @Override
    public void dispatch(ServletContext context, String path) {
        synchronized (this) {
            this.dispatchedPath = path;
            this.dispatchHandlers.forEach(Runnable::run);
        }
    }


    public String getDispatchedPath() {
        return this.dispatchedPath;
    }

    @Override
    public void complete() {
        SimpleHttpServletRequest mockRequest = WebUtils.getNativeRequest(this.request, SimpleHttpServletRequest.class);
        if (mockRequest != null) {
            mockRequest.setAsyncStarted(false);
        }
        for (AsyncListener listener : this.listeners) {
            try {
                listener.onComplete(new AsyncEvent(this, this.request, this.response));
            } catch (IOException ex) {
                throw new IllegalStateException("AsyncListener failure", ex);
            }
        }
    }

    @Override
    public void start(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void addListener(AsyncListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest request, ServletResponse response) {
        this.listeners.add(listener);
    }

    public List<AsyncListener> getListeners() {
        return this.listeners;
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        return BeanUtils.instantiateClass(clazz);
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * By default this is set to 10000 (10 seconds) even though the Servlet API
     * specifies a default async request timeout of 30 seconds. Keep in mind the
     * timeout could further be impacted by global configuration through the MVC
     * Java config or the XML namespace, as well as be overridden per request on
     * {@link org.springframework.web.context.request.async.DeferredResult DeferredResult}
     * or on
     *
     * @param timeout the timeout value to use.
     * @see AsyncContext#setTimeout(long)
     */
    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
