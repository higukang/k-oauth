package kr.higu.dto.kakao;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for Kakao OAuth refresh token grant.
 * Maps the response from the "Refresh token" section of the Kakao Login REST API.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#refresh-token">Kakao Refresh Token Documentation</a>
 */
public record KakaoRefreshTokenResponse(
        /** Token type, fixed to bearer according to the Kakao spec. */
        @SerializedName("token_type") String tokenType,
        /** Refreshed user access token value. */
        @SerializedName("access_token") String accessToken,
        /**
         * Refreshed ID token value.
         * Only included when refreshing a token originally issued with an OpenID Connect ID token.
         */
        @SerializedName("id_token") String idToken,
        /** Expiry time of the access token in seconds. */
        @SerializedName("expires_in") Integer expiresIn,
        /**
         * Refreshed user refresh token value.
         * Only included when the current refresh token has less than one month remaining.
         */
        @SerializedName("refresh_token") String refreshToken,
        /**
         * Expiry time of the refreshed refresh token in seconds.
         * Only included when the current refresh token has less than one month remaining.
         */
        @SerializedName("refresh_token_expires_in") Integer refreshTokenExpiresIn
) {}
