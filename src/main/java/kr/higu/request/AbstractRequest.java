package kr.higu.request;

import com.google.gson.Gson;
import kr.higu.IHttpManager;
import kr.higu.exceptions.OAuthException;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.exceptions.detailed.OAuthParsingException;
import kr.higu.exceptions.detailed.OAuthResponseException;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Abstract base class for all OAuth-related API requests.
 * Provides common functionality for building URIs, managing headers, and executing requests.
 *
 * <p>This class uses the Template Method pattern to define the skeleton of an OAuth request
 * while delegating specific details (URI, method, error parsing) to subclasses.</p>
 *
 * @param <T> The type of the response object this request expects.
 * @author higukang
 */
public abstract class AbstractRequest<T> {
    private static final Gson GSON = new Gson();

    protected final IHttpManager httpManager;
    protected final Map<String, String> headers;
    protected final Map<String, String> params;
    protected final Class<T> responseType;

    /**
     * Constructs an AbstractRequest using the provided builder.
     *
     * @param builder The builder containing request configurations.
     */
    protected AbstractRequest(Builder<T, ?> builder) {
        this.httpManager = builder.httpManager;
        this.headers = new HashMap<>(builder.headers);
        this.params = new HashMap<>(builder.params);
        this.responseType = builder.responseType;
    }

    /**
     * Abstract Builder class for creating request instances.
     * Uses recursive generics to support a fluent API in subclasses.
     *
     * @param <T>  The response type.
     * @param <BT> The concrete builder type.
     */
    public static abstract class Builder<T, BT extends Builder<T, BT>> {
        protected final IHttpManager httpManager;
        protected final Class<T> responseType;
        protected final Map<String, String> headers = new HashMap<>();
        protected final Map<String, String> params = new HashMap<>();

        protected Builder(IHttpManager httpManager, Class<T> responseType) {
            this.httpManager = httpManager;
            this.responseType = responseType;
        }

        protected BT setHeader(String name, String value) {
            headers.put(name, value);
            return self();
        }

        protected BT addParam(String name, String value) {
            params.put(name, value);
            return self();
        }

        /** @return The concrete builder instance (this). */
        protected abstract BT self();

        /**
         * Validates that all required parameters are present in the params map.
         *
         * @param requiredParams Array of parameter names that must not be null or blank.
         * @throws kr.higu.exceptions.OAuthValidationException If any of the required parameters are missing or blank.
         */
        protected void validate(String... requiredParams) {
            for (String param : requiredParams) {
                if (!params.containsKey(param) || params.get(param).isBlank()) {
                    throw new OAuthValidationException(
                            String.format("[K-OAuth]: Required parameter [%s] is missing.", param)
                    );
                }
            }
        }

        /**
         * Builds and returns the concrete request instance.
         *
         * @return The request instance.
         * @throws OAuthException If building the request fails.
         */
        public abstract AbstractRequest<T> build() throws OAuthException;
    }

    /** @return The HTTP method (e.g., "GET", "POST"). */
    protected abstract String getMethod();

    /** @return The base URI of the request. */
    protected abstract URI getUri();

    /**
     * Parses a provider-specific error response into a common {@link ErrorDetail}.
     *
     * @param errorBody The raw response body from the OAuth provider.
     * @return A parsed ErrorDetail object.
     */
    protected abstract ErrorDetail parseError(String errorBody);

    /**
     * Executes the request and returns the parsed response.
     *
     * @return The parsed response of type T.
     * @throws OAuthException If any error occurs during the request or parsing.
     */
    public T execute() throws OAuthException {
        try {
            String paramsString = buildQueryParams();
            String response;
            if (getMethod().equals("GET")) {
                URI finalUri = buildFinalUri(paramsString);
                response = httpManager.get(finalUri, headers);
            } else {
                response = httpManager.post(getUri(), headers, paramsString);
            }
            validateSuccessResponse(response);
            try {
                return GSON.fromJson(response, responseType);
            } catch (Exception e) {
                throw new OAuthParsingException(
                        String.format("[K-OAuth] Failed to parse %s response: %s",
                                responseType.getSimpleName(), e.getMessage()), e
                );
            }

        } catch (OAuthResponseException e) {
            ErrorDetail detail = parseError(e.getRawBody());
            throw new OAuthResponseException(
                    e.getStatusCode(),
                    detail.errorCode(),
                    e.getRawBody(),
                    detail.message()
            );
        }
    }

    /**
     * Safely combines the base URI and query parameters.
     */
    private URI buildFinalUri(String paramsString) {
        if (paramsString.isEmpty()) {
            return getUri();
        }

        String baseUri = getUri().toString();
        String connector = baseUri.contains("?") ? "&" : "?";

        return URI.create(baseUri + connector + paramsString);
    }

    /**
     * Encodes parameters into an x-www-form-urlencoded string.
     */
    private String buildQueryParams() {
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            joiner.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                    URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return joiner.toString();
    }

    /**
     * Optional hook to validate responses that return 2xx but are logically errors (e.g., Naver).
     *
     * @param responseBody The raw response body.
     * @throws OAuthException If the response is determined to be an error.
     */
    protected void validateSuccessResponse(String responseBody) throws OAuthException {}
}
