/*
 * Adapterd from spring-framework test
 */

package com.bixuebihui.jmesa.mock;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of the {@link javax.servlet.http.HttpServletRequest} interface.
 *
 * <p>The default, preferred {@link Locale} for the <em>server</em> mocked by this request
 * is {@link Locale#ENGLISH}. This value can be changed via {@link #addPreferredLocale}
 * or {@link #setPreferredLocales}.
 *
 * <p>As of Spring Framework 5.0, this set of mocks is designed on a Servlet 4.0 baseline.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Rick Evans
 * @author Mark Fisher
 * @author Chris Beams
 * @author Sam Brannen
 * @author Brian Clozel
 * @since 1.0.2
 */
public class SimpleHttpServletRequest implements HttpServletRequest {

    /**
     * The default protocol: 'HTTP/1.1'.
     *
     * @since 4.3.7
     */
    public static final String DEFAULT_PROTOCOL = "HTTP/1.1";
    /**
     * The default server address: '127.0.0.1'.
     */
    public static final String DEFAULT_SERVER_ADDR = "127.0.0.1";
    /**
     * The default server name: 'localhost'.
     */
    public static final String DEFAULT_SERVER_NAME = "localhost";
    /**
     * The default server port: '80'.
     */
    public static final int DEFAULT_SERVER_PORT = 80;
    /**
     * The default remote address: '127.0.0.1'.
     */
    public static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";
    /**
     * The default remote host: 'localhost'.
     */
    public static final String DEFAULT_REMOTE_HOST = "localhost";


    /**
    * Public constants
    */
    private static final String HTTP = "http";
    /**
     * The default scheme: 'http'.
     *
     * @since 4.3.7
     */
    public static final String DEFAULT_SCHEME = HTTP;
    private static final String HTTPS = "https";
    private static final String CHARSET_PREFIX = "charset=";
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final BufferedReader EMPTY_BUFFERED_READER =
            new BufferedReader(new StringReader(""));
    /**
     * Date formats as specified in the HTTP RFC.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
     */
    private static final String[] DATE_FORMATS = new String[]{
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd-MMM-yy HH:mm:ss zzz",
            "EEE MMM dd HH:mm:ss yyyy"
    };


    // ---------------------------------------------------------------------
    // Lifecycle properties
    // ---------------------------------------------------------------------
    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private final Map<String, String[]> parameters = new LinkedHashMap<>(16);


    // ---------------------------------------------------------------------
    // ServletRequest properties
    // ---------------------------------------------------------------------
    /**
     * List of locales in descending order.
     */
    private final LinkedList<Locale> locales = new LinkedList<>();
    private final Map<String, HeaderValueHolder> headers = new LinkedCaseInsensitiveMap<>();
    private final Set<String> userRoles = new HashSet<>();
    private final MultiValueMap<String, Part> parts = new LinkedMultiValueMap<>();
    private ServletContext servletContext;
    private boolean active = true;
    private String characterEncoding;
    private byte[] content;
    private String contentType;
    private ServletInputStream inputStream;
    private BufferedReader reader;
    private String protocol = DEFAULT_PROTOCOL;
    private String scheme = DEFAULT_SCHEME;
    private String serverName = DEFAULT_SERVER_NAME;
    private int serverPort = DEFAULT_SERVER_PORT;
    private String remoteAddr = DEFAULT_REMOTE_ADDR;
    private String remoteHost = DEFAULT_REMOTE_HOST;
    private boolean secure = false;
    private int remotePort = DEFAULT_SERVER_PORT;
    private String localName = DEFAULT_SERVER_NAME;
    private String localAddr = DEFAULT_SERVER_ADDR;
    private int localPort = DEFAULT_SERVER_PORT;
    private boolean asyncStarted = false;


    // ---------------------------------------------------------------------
    // HttpServletRequest properties
    // ---------------------------------------------------------------------
    private boolean asyncSupported = false;
    private MockAsyncContext asyncContext;
    private DispatcherType dispatcherType = DispatcherType.REQUEST;
    private String authType;
    private Cookie[] cookies;
    private String method;
    private String pathInfo;
    private String contextPath = "";
    private String queryString;
    private String remoteUser;
    private Principal userPrincipal;
    private String requestedSessionId;
    private String requestURI;
    private String servletPath = "";
    private HttpSession session;
    private boolean requestedSessionIdValid = true;
    private boolean requestedSessionIdFromCookie = true;
    private boolean requestedSessionIdFromURL = false;


    // ---------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------

    /**
     * Create a new {@code MockHttpServletRequest} with a default
     *
     * @see #SimpleHttpServletRequest(ServletContext, String, String)
     */
    public SimpleHttpServletRequest() {
        this(null, "", "");
    }

    /**
     * Create a new {@code MockHttpServletRequest} with a default
     *
     * @param method     the request method (may be {@code null})
     * @param requestURI the request URI (may be {@code null})
     * @see #setMethod
     * @see #setRequestURI
     * @see #SimpleHttpServletRequest(ServletContext, String, String)
     */
    public SimpleHttpServletRequest(String method, String requestURI) {
        this(null, method, requestURI);
    }

    /**
     * Create a new {@code MockHttpServletRequest} with the supplied {@link ServletContext}.
     *
     * @param servletContext the ServletContext that the request runs in
     * @see #SimpleHttpServletRequest(ServletContext, String, String)
     */
    public SimpleHttpServletRequest(ServletContext servletContext) {
        this(servletContext, "", "");
    }

    /**
     * Create a new {@code MockHttpServletRequest} with the supplied {@link ServletContext},
     * {@code method}, and {@code requestURI}.
     * <p>The preferred locale will be set to {@link Locale#ENGLISH}.
     *
     * @param servletContext the ServletContext that the request runs in (may be
     * @param method         the request method (may be {@code null})
     * @param requestURI     the request URI (may be {@code null})
     * @see #setMethod
     * @see #setRequestURI
     * @see #setPreferredLocales
     */
    public SimpleHttpServletRequest(ServletContext servletContext, String method, String requestURI) {
        //this.servletContext = (servletContext != null ? servletContext : new MockServletContext());
        this.method = method;
        this.requestURI = requestURI;
        this.locales.add(Locale.ENGLISH);
    }


    // ---------------------------------------------------------------------
    // Lifecycle methods
    // ---------------------------------------------------------------------

    private static String encodeCookies(Cookie... cookies) {
        return Arrays.stream(cookies)
                .map(c -> c.getName() + '=' + (c.getValue() == null ? "" : c.getValue()))
                .collect(Collectors.joining("; "));
    }

    /**
     * Return the ServletContext that this request is associated with. (Not
     * available in the standard HttpServletRequest interface for some reason.)
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    /**
     * Return whether this request is still active (that is, not completed yet).
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Mark this request as completed, keeping its state.
     */
    public void close() {
        this.active = false;
    }

    /**
     * Invalidate this request, clearing its state.
     */
    public void invalidate() {
        close();
        clearAttributes();
    }


    // ---------------------------------------------------------------------
    // ServletRequest interface
    // ---------------------------------------------------------------------

    /**
     * Check whether this request is still active (that is, not completed yet),
     * throwing an IllegalStateException if not active anymore.
     */
    protected void checkActive() throws IllegalStateException {
        Assert.state(this.active, "Request is not active anymore");
    }

    @Override
    public Object getAttribute(String name) {
        checkActive();
        return this.attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkActive();
        return Collections.enumeration(new LinkedHashSet<>(this.attributes.keySet()));
    }

    @Override

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
        updateContentTypeHeader();
    }

    private void updateContentTypeHeader() {
        if (StringUtils.hasLength(this.contentType)) {
            String value = this.contentType;
            if (StringUtils.hasLength(this.characterEncoding) && !this.contentType.toLowerCase().contains(CHARSET_PREFIX)) {
                value += ';' + CHARSET_PREFIX + this.characterEncoding;
            }
            doAddHeaderValue(HttpHeaders.CONTENT_TYPE, value, true);
        }
    }

    /**
     * Set the content of the request body as a byte array.
     * <p>If the supplied byte array represents text such as XML or JSON, the
     * {@link #setCharacterEncoding character encoding} should typically be
     * set as well.
     *
     * @see #setCharacterEncoding(String)
     * @see #getContentAsByteArray()
     * @see #getContentAsString()
     */
    public void setContent(byte[] content) {
        this.content = content;
        this.inputStream = null;
        this.reader = null;
    }

    /**
     * Get the content of the request body as a byte array.
     *
     * @return the content as a byte array (potentially {@code null})
     * @see #setContent(byte[])
     * @see #getContentAsString()
     * @since 5.0
     */

    public byte[] getContentAsByteArray() {
        return this.content;
    }

    /**
     * Get the content of the request body as a {@code String}, using the configured
     * {@linkplain #getCharacterEncoding character encoding}.
     *
     * @return the content as a {@code String}, potentially {@code null}
     * @throws IllegalStateException        if the character encoding has not been set
     * @throws UnsupportedEncodingException if the character encoding is not supported
     * @see #setContent(byte[])
     * @see #setCharacterEncoding(String)
     * @see #getContentAsByteArray()
     * @since 5.0
     */

    public String getContentAsString() throws IllegalStateException, UnsupportedEncodingException {
        Assert.state(this.characterEncoding != null,
                "Cannot get content as a String for a null character encoding. " +
                        "Consider setting the characterEncoding in the request.");

        if (this.content == null) {
            return null;
        }
        return new String(this.content, this.characterEncoding);
    }

    @Override
    public int getContentLength() {
        return (this.content != null ? this.content.length : -1);
    }

    @Override
    public long getContentLengthLong() {
        return getContentLength();
    }

    @Override

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        if (contentType != null) {
            try {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                if (mediaType.getCharset() != null) {
                    this.characterEncoding = mediaType.getCharset().name();
                }
            } catch (IllegalArgumentException ex) {
                // Try to get charset value anyway
                int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
                if (charsetIndex != -1) {
                    this.characterEncoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
                }
            }
            updateContentTypeHeader();
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        if (this.inputStream != null) {
            return this.inputStream;
        } else if (this.reader != null) {
            throw new IllegalStateException(
                    "Cannot call getInputStream() after getReader() has already been called for the current request");
        }

//		this.inputStream = (this.content != null ?
//				new DelegatingServletInputStream(new ByteArrayInputStream(this.content)) :
//				EMPTY_SERVLET_INPUT_STREAM);
        return this.inputStream;
    }

    /**
     * Set a single value for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, they will be replaced.
     */
    public void setParameter(String name, String value) {
        setParameter(name, new String[]{value});
    }

    /**
     * Set an array of values for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, they will be replaced.
     */
    public void setParameter(String name, String... values) {
        Assert.notNull(name, "Parameter name must not be null");
        this.parameters.put(name, values);
    }

    /**
     * Set all provided parameters <strong>replacing</strong> any existing
     * values for the provided parameter names. To add without replacing
     * existing values, use {@link #addParameters(java.util.Map)}.
     */
    public void setParameters(Map<String, ?> params) {
        Assert.notNull(params, "Parameter map must not be null");
        params.forEach((key, value) -> {
            if (value instanceof String) {
                setParameter(key, (String) value);
            } else if (value instanceof String[]) {
                setParameter(key, (String[]) value);
            } else {
                throw new IllegalArgumentException(
                        "Parameter map value must be single value " + " or array of type [" + String.class.getName() + "]");
            }
        });
    }

    /**
     * Add a single value for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, the given value will be added to the end of the list.
     */
    public void addParameter(String name, String value) {
        addParameter(name, new String[]{value});
    }

    /**
     * Add an array of values for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, the given values will be added to the end of the list.
     */
    public void addParameter(String name, String... values) {
        Assert.notNull(name, "Parameter name must not be null");
        String[] oldArr = this.parameters.get(name);
        if (oldArr != null) {
            String[] newArr = new String[oldArr.length + values.length];
            System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
            System.arraycopy(values, 0, newArr, oldArr.length, values.length);
            this.parameters.put(name, newArr);
        } else {
            this.parameters.put(name, values);
        }
    }

    /**
     * Add all provided parameters <strong>without</strong> replacing any
     * existing values. To replace existing values, use
     * {@link #setParameters(java.util.Map)}.
     */
    public void addParameters(Map<String, ?> params) {
        Assert.notNull(params, "Parameter map must not be null");
        params.forEach((key, value) -> {
            if (value instanceof String) {
                addParameter(key, (String) value);
            } else if (value instanceof String[]) {
                addParameter(key, (String[]) value);
            } else {
                throw new IllegalArgumentException("Parameter map value must be single value " +
                        " or array of type [" + String.class.getName() + "]");
            }
        });
    }

    /**
     * Remove already registered values for the specified HTTP parameter, if any.
     */
    public void removeParameter(String name) {
        Assert.notNull(name, "Parameter name must not be null");
        this.parameters.remove(name);
    }

    /**
     * Remove all existing parameters.
     */
    public void removeAllParameters() {
        this.parameters.clear();
    }

    @Override

    public String getParameter(String name) {
        Assert.notNull(name, "Parameter name must not be null");
        String[] arr = this.parameters.get(name);
        return (arr != null && arr.length > 0 ? arr[0] : null);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        Assert.notNull(name, "Parameter name must not be null");
        return this.parameters.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(this.parameters);
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String getServerName() {
        String host = getHeader(HttpHeaders.HOST);
        if (host != null) {
            host = host.trim();
            if (host.startsWith("[")) {
                host = host.substring(1, host.indexOf(']'));
            } else if (host.contains(":")) {
                host = host.substring(0, host.indexOf(':'));
            }
            return host;
        }

        // else
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public int getServerPort() {
        String host = getHeader(HttpHeaders.HOST);
        if (host != null) {
            host = host.trim();
            int idx;
            if (host.startsWith("[")) {
                idx = host.indexOf(':', host.indexOf(']'));
            } else {
                idx = host.indexOf(':');
            }
            if (idx != -1) {
                return Integer.parseInt(host.substring(idx + 1));
            }
        }

        // else
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public BufferedReader getReader() throws UnsupportedEncodingException {
        if (this.reader != null) {
            return this.reader;
        } else if (this.inputStream != null) {
            throw new IllegalStateException(
                    "Cannot call getReader() after getInputStream() has already been called for the current request");
        }

        if (this.content != null) {
            InputStream sourceStream = new ByteArrayInputStream(this.content);
            Reader sourceReader = (this.characterEncoding != null) ?
                    new InputStreamReader(sourceStream, this.characterEncoding) :
                    new InputStreamReader(sourceStream);
            this.reader = new BufferedReader(sourceReader);
        } else {
            this.reader = EMPTY_BUFFERED_READER;
        }
        return this.reader;
    }

    @Override
    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkActive();
        Assert.notNull(name, "Attribute name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            this.attributes.remove(name);
        }
    }

    @Override
    public void removeAttribute(String name) {
        checkActive();
        Assert.notNull(name, "Attribute name must not be null");
        this.attributes.remove(name);
    }

    /**
     * Clear all of this request's attributes.
     */
    public void clearAttributes() {
        this.attributes.clear();
    }

    /**
     * Add a new preferred locale, before any existing locales.
     *
     * @see #setPreferredLocales
     */
    public void addPreferredLocale(Locale locale) {
        Assert.notNull(locale, "Locale must not be null");
        this.locales.addFirst(locale);
        updateAcceptLanguageHeader();
    }

    /**
     * Set the list of preferred locales, in descending order, effectively replacing
     * any existing locales.
     *
     * @see #addPreferredLocale
     * @since 3.2
     */
    public void setPreferredLocales(List<Locale> locales) {
        Assert.notEmpty(locales, "Locale list must not be empty");
        this.locales.clear();
        this.locales.addAll(locales);
        updateAcceptLanguageHeader();
    }

    private void updateAcceptLanguageHeader() {
        HttpHeaders headers = new HttpHeaders();
        doAddHeaderValue(HttpHeaders.ACCEPT_LANGUAGE, headers.getFirst(HttpHeaders.ACCEPT_LANGUAGE), true);
    }

    /**
     * Return the first preferred {@linkplain Locale locale} configured
     * in this mock request.
     * <p>If no locales have been explicitly configured, the default,
     * preferred {@link Locale} for the <em>server</em> mocked by this
     * request is {@link Locale#ENGLISH}.
     * <p>In contrast to the Servlet specification, this mock implementation
     * does <strong>not</strong> take into consideration any locales
     * specified via the {@code Accept-Language} header.
     *
     * @see javax.servlet.ServletRequest#getLocale()
     * @see #addPreferredLocale(Locale)
     * @see #setPreferredLocales(List)
     */
    @Override
    public Locale getLocale() {
        return this.locales.getFirst();
    }

    /**
     * Return an {@linkplain Enumeration enumeration} of the preferred
     * {@linkplain Locale locales} configured in this mock request.
     * <p>If no locales have been explicitly configured, the default,
     * preferred {@link Locale} for the <em>server</em> mocked by this
     * request is {@link Locale#ENGLISH}.
     * <p>In contrast to the Servlet specification, this mock implementation
     * does <strong>not</strong> take into consideration any locales
     * specified via the {@code Accept-Language} header.
     *
     * @see javax.servlet.ServletRequest#getLocales()
     * @see #addPreferredLocale(Locale)
     * @see #setPreferredLocales(List)
     */
    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(this.locales);
    }

    /**
     * Return {@code true} if the {@link #setSecure secure} flag has been set
     * to {@code true} or if the {@link #getScheme scheme} is {@code https}.
     *
     * @see javax.servlet.ServletRequest#isSecure()
     */
    @Override
    public boolean isSecure() {
        return (this.secure || HTTPS.equalsIgnoreCase(this.scheme));
    }

    /**
     * Set the boolean {@code secure} flag indicating whether the mock request
     * was made using a secure channel, such as HTTPS.
     *
     * @see #isSecure()
     * @see #getScheme()
     * @see #setScheme(String)
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new NotImplementedException();
    }

    @Override
    @Deprecated
    public String getRealPath(String path) {
        return this.servletContext.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return this.remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    @Override
    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String getLocalAddr() {
        return this.localAddr;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    @Override
    public int getLocalPort() {
        return this.localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    @Override
    public AsyncContext startAsync() {
        return startAsync(this, null);
    }

    @Override
    public AsyncContext startAsync(ServletRequest request, ServletResponse response) {
        Assert.state(this.asyncSupported, "Async not supported");
        this.asyncStarted = true;
        this.asyncContext = new MockAsyncContext(request, response);
        return this.asyncContext;
    }

    @Override
    public boolean isAsyncStarted() {
        return this.asyncStarted;
    }

    public void setAsyncStarted(boolean asyncStarted) {
        this.asyncStarted = asyncStarted;
    }

    @Override
    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    @Override

    public AsyncContext getAsyncContext() {
        return this.asyncContext;
    }

    public void setAsyncContext(MockAsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return this.dispatcherType;
    }


    // ---------------------------------------------------------------------
    // HttpServletRequest interface
    // ---------------------------------------------------------------------

    public void setDispatcherType(DispatcherType dispatcherType) {
        this.dispatcherType = dispatcherType;
    }

    @Override

    public String getAuthType() {
        return this.authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override

    public Cookie[] getCookies() {
        return this.cookies;
    }

    public void setCookies(Cookie... cookies) {
        this.cookies = (ObjectUtils.isEmpty(cookies) ? null : cookies);
        if (this.cookies == null) {
            removeHeader(HttpHeaders.COOKIE);
        } else {
            doAddHeaderValue(HttpHeaders.COOKIE, encodeCookies(this.cookies), true);
        }
    }

    /**
     * Add an HTTP header entry for the given name.
     * <p>While this method can take any {@code Object} as a parameter,
     * it is recommended to use the following types:
     * <ul>
     * <li>String or any Object to be converted using {@code toString()}; see {@link #getHeader}.</li>
     * <li>String, Number, or Date for date headers; see {@link #getDateHeader}.</li>
     * <li>String or Number for integer headers; see {@link #getIntHeader}.</li>
     * <li>{@code String[]} or {@code Collection<String>} for multiple values; see {@link #getHeaders}.</li>
     * </ul>
     *
     * @see #getHeaderNames
     * @see #getHeaders
     * @see #getHeader
     * @see #getDateHeader
     */
    public void addHeader(String name, Object value) {
        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name) &&
                !this.headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            setContentType(value.toString());
        } else if (HttpHeaders.ACCEPT_LANGUAGE.equalsIgnoreCase(name) &&
                !this.headers.containsKey(HttpHeaders.ACCEPT_LANGUAGE)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.ACCEPT_LANGUAGE, value.toString());
                this.locales.clear();
                this.locales.addAll(locales);
                if (this.locales.isEmpty()) {
                    this.locales.add(Locale.ENGLISH);
                }
            } catch (IllegalArgumentException ex) {
                // Invalid Accept-Language format -> just store plain header
            }
            doAddHeaderValue(name, value, true);
        } else {
            doAddHeaderValue(name, value, false);
        }
    }

    private void doAddHeaderValue(String name, Object value, boolean replace) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Assert.notNull(value, "Header value must not be null");
        if (header == null || replace) {
            header = new HeaderValueHolder();
            this.headers.put(name, header);
        }
        if (value instanceof Collection) {
            header.addValues((Collection<?>) value);
        } else if (value.getClass().isArray()) {
            header.addValueArray(value);
        } else {
            header.addValue(value);
        }
    }

    /**
     * Remove already registered entries for the specified HTTP header, if any.
     *
     * @since 4.3.20
     */
    public void removeHeader(String name) {
        Assert.notNull(name, "Header name must not be null");
        this.headers.remove(name);
    }

    /**
     * Return the long timestamp for the date header with the given {@code name}.
     * <p>If the internal value representation is a String, this method will try
     * to parse it as a date using the supported date formats:
     * <ul>
     * <li>"EEE, dd MMM yyyy HH:mm:ss zzz"</li>
     * <li>"EEE, dd-MMM-yy HH:mm:ss zzz"</li>
     * <li>"EEE MMM dd HH:mm:ss yyyy"</li>
     * </ul>
     *
     * @param name the header name
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
     */
    @Override
    public long getDateHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object value = (header != null ? header.getValue() : null);
        if (value instanceof Date) {
            return ((Date) value).getTime();
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            return parseDateHeader(name, (String) value);
        } else if (value != null) {
            throw new IllegalArgumentException(
                    "Value for header '" + name + "' is not a Date, Number, or String: " + value);
        } else {
            return -1L;
        }
    }

    private long parseDateHeader(String name, String value) {
        for (String dateFormat : DATE_FORMATS) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            simpleDateFormat.setTimeZone(GMT);
            try {
                return simpleDateFormat.parse(value).getTime();
            } catch (ParseException ex) {
                // ignore
            }
        }
        throw new IllegalArgumentException("Cannot parse date value '" + value + "' for '" + name + "' header");
    }

    @Override

    public String getHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return (header != null ? header.getStringValue() : null);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return Collections.enumeration(header != null ? header.getStringValues() : new LinkedList<>());
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(this.headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Object value = (header != null ? header.getValue() : null);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else if (value != null) {
            throw new NumberFormatException("Value for header '" + name + "' is not a Number: " + value);
        } else {
            return -1;
        }
    }

    @Override

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override

    public String getPathInfo() {
        return this.pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    @Override

    public String getPathTranslated() {
        return (this.pathInfo != null ? getRealPath(this.pathInfo) : null);
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override

    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override

    public String getRemoteUser() {
        return this.remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public void addUserRole(String role) {
        this.userRoles.add(role);
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new NotImplementedException();
    }

    @Override

    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    @Override

    public String getRequestedSessionId() {
        return this.requestedSessionId;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    @Override

    public String getRequestURI() {
        return this.requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        String scheme = getScheme();
        String server = getServerName();
        int port = getServerPort();
        String uri = getRequestURI();

        StringBuffer url = new StringBuffer(scheme).append("://").append(server);
        if (port > 0 && ((HTTP.equalsIgnoreCase(scheme) && port != 80) ||
                (HTTPS.equalsIgnoreCase(scheme) && port != 443))) {
            url.append(':').append(port);
        }
        if (StringUtils.hasText(uri)) {
            url.append(uri);
        }
        return url;
    }

    @Override
    public String getServletPath() {
        return this.servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return this.session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    /**
     * The implementation of this (Servlet 3.1+) method calls
     * Otherwise it simply returns the current session id.
     *
     * @since 4.0.3
     */
    @Override
    public String changeSessionId() {
        return this.session.getId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return this.requestedSessionIdValid;
    }

    public void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
        this.requestedSessionIdValid = requestedSessionIdValid;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return this.requestedSessionIdFromCookie;
    }

    public void setRequestedSessionIdFromCookie(boolean requestedSessionIdFromCookie) {
        this.requestedSessionIdFromCookie = requestedSessionIdFromCookie;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return this.requestedSessionIdFromURL;
    }

    public void setRequestedSessionIdFromURL(boolean requestedSessionIdFromURL) {
        this.requestedSessionIdFromURL = requestedSessionIdFromURL;
    }

    @Override
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        this.userPrincipal = null;
        this.remoteUser = null;
        this.authType = null;
    }

    public void addPart(Part part) {
        this.parts.add(part.getName(), part);
    }

    @Override

    public Part getPart(String name) throws IOException, ServletException {
        return this.parts.getFirst(name);
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        List<Part> result = new LinkedList<>();
        for (List<Part> list : this.parts.values()) {
            result.addAll(list);
        }
        return result;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }


    /**
     * Internal helper class that serves as value holder for request headers.
     *
     * @author Juergen Hoeller
     * @author Rick Evans
     * @since 2.0.1
     */
    public static class HeaderValueHolder {

        private final List<Object> values = new LinkedList<>();

        /**
         * Find a HeaderValueHolder by name, ignoring casing.
         *
         * @param headers the Map of header names to HeaderValueHolders
         * @param name    the name of the desired header
         * @return the corresponding HeaderValueHolder, or {@code null} if none found
         */

        public static HeaderValueHolder getByName(Map<String, HeaderValueHolder> headers, String name) {
            Assert.notNull(name, "Header name must not be null");
            for (String headerName : headers.keySet()) {
                if (headerName.equalsIgnoreCase(name)) {
                    return headers.get(headerName);
                }
            }
            return null;
        }

        public void addValue(Object value) {
            this.values.add(value);
        }

        public void addValues(Collection<?> values) {
            this.values.addAll(values);
        }

        public void addValueArray(Object values) {
            CollectionUtils.mergeArrayIntoCollection(values, this.values);
        }

        public List<Object> getValues() {
            return Collections.unmodifiableList(this.values);
        }

        public List<String> getStringValues() {
            List<String> stringList = new ArrayList<>(this.values.size());
            for (Object value : this.values) {
                stringList.add(value.toString());
            }
            return Collections.unmodifiableList(stringList);
        }


        public Object getValue() {
            return (!this.values.isEmpty() ? this.values.get(0) : null);
        }

        public void setValue(Object value) {
            this.values.clear();
            if (value != null) {
                this.values.add(value);
            }
        }

        public String getStringValue() {
            return (!this.values.isEmpty() ? String.valueOf(this.values.get(0)) : null);
        }

        @Override
        public String toString() {
            return this.values.toString();
        }

    }

    public class DelegatingServletInputStream extends ServletInputStream {

        private final InputStream sourceStream;

        private boolean finished = false;


        /**
         * Create a DelegatingServletInputStream for the given source stream.
         *
         * @param sourceStream the source stream (never {@code null})
         */
        public DelegatingServletInputStream(InputStream sourceStream) {
            Assert.notNull(sourceStream, "Source InputStream must not be null");
            this.sourceStream = sourceStream;
        }

        /**
         * Return the underlying source stream (never {@code null}).
         */
        public final InputStream getSourceStream() {
            return this.sourceStream;
        }


        @Override
        public int read() throws IOException {
            int data = this.sourceStream.read();
            if (data == -1) {
                this.finished = true;
            }
            return data;
        }

        @Override
        public int available() throws IOException {
            return this.sourceStream.available();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

        @Override
        public boolean isFinished() {
            return this.finished;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

    }

}
