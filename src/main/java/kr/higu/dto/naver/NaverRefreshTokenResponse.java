package kr.higu.dto.naver;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for the "Access token refresh request" output fields in the Naver Login API.
 *
 * @see <a href="https://developers.naver.com/docs/login/api/api.md">Naver Refresh Token Documentation</a>
 * @author higukang
 */
public record NaverRefreshTokenResponse(
        /** The refreshed token used to access protected resources. */
        @SerializedName("access_token") String accessToken,
        /** Type of the token (e.g., "bearer"). */
        @SerializedName("token_type") String tokenType,
        /** Lifetime of the access token in seconds. */
        @SerializedName("expires_in") String expiresIn,
        /** Error code returned by Naver. */
        @SerializedName("error") String error,
        /** Error description returned by Naver. */
        @SerializedName("error_description") String errorDescription
) {}
