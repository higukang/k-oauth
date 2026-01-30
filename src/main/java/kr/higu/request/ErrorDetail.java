package kr.higu.request;

/**
 * INTERNAL USE ONLY.
 * <p>
 * This record is public only to allow access from sub-packages.
 * It is not intended for use by library users.
 * <p>
 * Internal data carrier for parsed error information from OAuth providers.
 * <p>
 * This record is used to bridge the gap between raw response parsing
 * and the final {@link kr.higu.exceptions.detailed.OAuthResponseException}.
 *
 * @param errorCode The specific error code from the provider (e.g., "KOE400").
 * @param message   The human-readable error message.
 */
public record ErrorDetail(
        String errorCode,
        String message) {}