package kr.higu.exceptions.detailed;

import kr.higu.exceptions.OAuthException;

/**
 * Exception thrown when a network-level error occurs during an OAuth request.
 * <p>
 * This exception wraps low-level I/O failures such as connection timeouts,
 * DNS lookup failures, or lost connectivity while communicating with the OAuth provider.
 * </p>
 *
 * @author higukang
 */
public class OAuthNetworkException extends OAuthException {

    /**
     * Constructs a new OAuthNetworkException with a detail message and the underlying cause.
     *
     * @param message The detail message explaining the network failure.
     * @param cause   The original {@link java.io.IOException} that triggered this exception.
     */
    public OAuthNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}

