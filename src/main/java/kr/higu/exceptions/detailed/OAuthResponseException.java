package kr.higu.exceptions.detailed;

import kr.higu.exceptions.OAuthException;

/**
 * Exception thrown when the OAuth server returns a non-2xx HTTP status code.
 * It contains detailed information about the error response from the provider.
 *
 * @author higukang
 */
public class OAuthResponseException extends OAuthException {
    private final int statusCode;
    private final String errorCode;
    private final String rawBody;

    /**
     * Constructs a new OauthResponseException with detailed error information.
     *
     * @param statusCode The HTTP status code returned by the server.
     * @param errorCode  The specific error code returned by the OAuth provider (e.g., "KOE101").
     * @param rawBody    The raw response body from the server.
     * @param message    The human-readable error message, often parsed from the response.
     */
    public OAuthResponseException(int statusCode, String errorCode, String rawBody, String message) {
        super(String.format("[OAuth Error] HTTP %d (%s): %s", statusCode, errorCode, message));
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.rawBody = rawBody;
    }

    /**
     * @return The HTTP status code (e.g., 400, 401, 500).
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return The specific error code from the OAuth provider. Might be null if not provided.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @return The raw response body string for debugging purposes.
     */
    public String getRawBody() {
        return rawBody;
    }
}
