package kr.higu.dto.kakao;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for Kakao OAuth token request.
 * Contains access tokens, refresh tokens, and OIDC ID tokens.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response">Kakao Token Response Documentation</a>
 */
public record KakaoTokenResponse(
        @SerializedName("token_type") String tokenType,
        @SerializedName("access_token") String accessToken,
        /** OpenID Connect ID token. Only returned if 'openid' scope is requested. */
        @SerializedName("id_token") String idToken,
        /** Expiry time of the access token in seconds. */
        @SerializedName("expires_in") Integer expiresIn,
        @SerializedName("refresh_token") String refreshToken,
        /** Expiry time of the refresh token in seconds. */
        @SerializedName("refresh_token_expires_in") Integer refreshTokenExpiresIn,
        @SerializedName("scope") String scope
) {}
