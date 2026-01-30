package kr.higu.request.kakao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.higu.IHttpManager;
import kr.higu.dto.kakao.KakaoUserResponse;
import kr.higu.exceptions.OAuthException;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.request.AbstractRequest;
import kr.higu.request.ErrorDetail;

import java.net.URI;

/**
 * Request class for retrieving the profile and account information of the authenticated Kakao user.
 * It accesses the Kakao API Server (KApi) using a Bearer Access Token.
 *
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info">Kakao User Info API Documentation</a>
 * @author higukang
 */
public class KakaoUserRequest extends AbstractRequest<KakaoUserResponse> {

    private KakaoUserRequest(Builder builder) {
        super(builder);
    }

    /**
     * Builder for creating {@link KakaoUserRequest} instances.
     */
    public static class Builder extends AbstractRequest.Builder<KakaoUserResponse, Builder> {
        /**
         * Initializes the builder with default headers for Kakao API access.
         * * @param httpManager The HTTP manager to use for the request.
         */
        public Builder(IHttpManager httpManager) {
            super(httpManager, KakaoUserResponse.class);
            this.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        }

        /**
         * Sets the access token for authentication.
         * Automatically formats it as a Bearer token in the Authorization header.
         *
         * @param accessToken The Kakao access token.
         * @return This builder instance.
         */
        public Builder accessToken(String accessToken) {
            return setHeader("Authorization", "Bearer " + accessToken);
        }

        /**
         * Sets whether to receive image URLs via HTTPS.
         * Default is false (returns HTTP URLs).
         *
         * @param secureResource Set to true to receive HTTPS URLs.
         * @return This builder instance.
         */
        public Builder secureResource (Boolean secureResource) {
            if (secureResource == null) {
                return self();
            }

            return addParam("secure_resource", String.valueOf(secureResource));
        }

        /**
         * Sets the specific property keys to retrieve.
         * If specified, only the corresponding fields will be included in the response.
         * Useful for reducing response size or focusing on specific data.
         *
         * @param keys One or more {@link KakaoPropertyKey} to request.
         * @return This builder instance.
         */
        public Builder propertyKeys(KakaoPropertyKey... keys) {
            if (keys == null || keys.length == 0) {
                return self();
            }
            String[] keyStrings = java.util.Arrays.stream(keys)
                    .map(KakaoPropertyKey::getKey)
                    .toArray(String[]::new);

            String jsonArray = new Gson().toJson(keyStrings);
            return addParam("property_keys", jsonArray);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Validates the presence of the Authorization header and builds the request.
         *
         * @return A new {@link KakaoUserRequest} instance.
         * @throws OAuthException If the Authorization header is missing or malformed.
         */
        @Override
        public KakaoUserRequest build() throws OAuthException {
            String authHeader = this.headers.get("Authorization");
            if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
                throw new OAuthValidationException("[kr-oauth] A valid access token is required to retrieve Kakao user information.");
            }
            return new KakaoUserRequest(this);
        }
    }

    @Override
    protected String getMethod() {
        return "GET";
    }

    @Override
    protected URI getUri() {
        return URI.create("https://kapi.kakao.com/v2/user/me");
    }

    /**
     * Parses the error response from Kakao API Server (kapi).
     * Unlike KAuth, KApi uses 'code' (Integer) and 'msg' (String) fields.
     *
     * @param errorBody The raw error JSON response.
     * @return An {@link ErrorDetail} containing the parsed error info.
     */
    @Override
    protected ErrorDetail parseError(String errorBody) {
        try {
            JsonObject json = JsonParser.parseString(errorBody).getAsJsonObject();

            String errorCode = json.has("code")
                    ? json.get("code").getAsString()
                    : "UNKNOWN_KAPI_ERROR";

            String message = json.has("msg")
                    ? json.get("msg").getAsString()
                    : "No error message provided.";

            return new ErrorDetail(errorCode, message);
        } catch (Exception e) {
            return new ErrorDetail("PARSING_ERROR", "Failed to parse kapi error: " + errorBody);
        }
    }
}
