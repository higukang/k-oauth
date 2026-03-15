package kr.higu.dto.kakao;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for Kakao OAuth authorization code token issuance.
 * Maps the response from the "Request token" section of the Kakao Login REST API.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response">Kakao Token Response Documentation</a>
 */
public record KakaoTokenResponse(
        @SerializedName("token_type") String tokenType,
        @SerializedName("access_token") String accessToken,
        /** OpenID Connect ID token. Only returned if the openid scope is requested. */
        @SerializedName("id_token") String idToken,
        /** Expiry time of the access token in seconds. */
        @SerializedName("expires_in") Integer expiresIn,
        /** Refresh token issued together with the access token. */
        @SerializedName("refresh_token") String refreshToken,
        /** Expiry time of the refresh token in seconds. */
        @SerializedName("refresh_token_expires_in") Integer refreshTokenExpiresIn,
        /** Granted scopes returned by Kakao during authorization code token issuance. */
        @SerializedName("scope") String scope
) {}
