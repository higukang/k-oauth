package kr.higu;

import kr.higu.exceptions.OAuthException;

import java.net.URI;
import java.util.Map;

/**
 * Interface for managing HTTP communications within the K-OAuth library.
 * It provides basic GET and POST methods to interact with OAuth providers' APIs.
 *
 * @author higukang
 */
public interface IHttpManager {
    /**
     * Executes an HTTP GET request to the specified URI.
     *
     * @param uri     The target URI for the request.
     * @param headers A map containing HTTP headers to include in the request. Can be null.
     * @return The response body as a String.
     * @throws OAuthException If the server returns an error response or a protocol-level error occurs.
     */
    String get(URI uri, Map<String, String> headers) throws OAuthException;

    /**
     * Executes an HTTP POST request to the specified URI with a request body.
     *
     * @param uri     The target URI for the request.
     * @param headers A map containing HTTP headers to include in the request. Can be null.
     * @param body    The request body to be sent (usually in x-www-form-urlencoded format).
     * @return The response body as a String.
     * @throws OAuthException If the server returns an error response or a protocol-level error occurs.
     */
    String post(URI uri, Map<String, String> headers, String body) throws OAuthException;
}
