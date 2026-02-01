package kr.higu.dto.naver;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for Naver OAuth token request.
 * Contains tokens and error information if the request fails with a 200 OK status.
 *
 * @see <a href="https://developers.naver.com/docs/login/api/api.md">Naver Token API Documentation</a>
 * @author higukang
 */
public record NaverTokenResponse(
        /** The token used to access protected resources. */
        @SerializedName("access_token") String accessToken,
        /** The token used to refresh the access token when it expires. */
        @SerializedName("refresh_token") String refreshToken,
        /** Type of the token (e.g., "bearer"). */
        @SerializedName("token_type") String tokenType,
        /** Lifetime of the access token in seconds. */
        @SerializedName("expires_in") String expiresIn,
        /** Error code (e.g., "invalid_request"). Only present if an error occurs. */
        @SerializedName("error") String error,
        /** Human-readable error description. Only present if an error occurs. */
        @SerializedName("error_description") String errorDescription
) {}
