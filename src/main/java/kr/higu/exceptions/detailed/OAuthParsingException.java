package kr.higu.exceptions.detailed;

import kr.higu.exceptions.OAuthException;

/**
 * Exception thrown when the response from the OAuth provider cannot be parsed
 * into the expected data transfer object (DTO).
 * <p>
 * This usually happens if the provider changes its API response format
 * or if there is a mismatch between the library's model and the actual JSON.
 * </p>
 *
 * @author higukang
 */
public class OAuthParsingException extends OAuthException {
    /**
     * Constructs a new OAuthParsingException with a detail message and the underlying cause.
     *
     * @param message The detail message explaining the parsing failure.
     * @param cause   The original cause (e.g., a JsonSyntaxException from Gson).
     */
    public OAuthParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}