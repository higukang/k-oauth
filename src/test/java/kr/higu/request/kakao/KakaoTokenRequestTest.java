package kr.higu.request.kakao;

import kr.higu.IHttpManager;
import kr.higu.dto.kakao.KakaoTokenResponse;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class KakaoTokenRequestTest {

    @Mock
    private IHttpManager httpManager;

    @Test
    @DisplayName("카카오 토큰 요청 성공")
    void execute_Success() throws Exception {
        // given
        String successJson = """
                {
                    "access_token": "i7RLoS0ZVfrJxNrAl5198F_1M-PQhATeAAAAAQoXEi0AAAGcCPgR8pgXPJRhmZ-F",
                    "token_type": "bearer",
                    "refresh_token": "9beQFNO2KWy3R8QKRf_-W1ZXs-FTyc3_AAAAAgoXEi0AAAGcCPgR7JgXPJRhmZ-F",
                    "expires_in": 21599,
                    "scope": "age_range account_email profile_image gender profile_nickname name",
                    "refresh_token_expires_in": 5183999
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(successJson);

        KakaoTokenRequest request = new KakaoTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .redirectUri("http://localhost")
                .code("TEST_CODE")
                .clientSecret("TEST_SECRET")
                .build();

        // when
        KakaoTokenResponse response = request.execute();

        // then
        assertThat(response.accessToken()).isEqualTo("i7RLoS0ZVfrJxNrAl5198F_1M-PQhATeAAAAAQoXEi0AAAGcCPgR8pgXPJRhmZ-F");
        assertThat(response.tokenType()).isEqualTo("bearer");
        assertThat(response.expiresIn()).isEqualTo(21599);
    }

    @Test
    @DisplayName("카카오 토큰 요청 실패 - 잘못된 코드 요청")
    void invalid_code() throws Exception {
        // given
        int statusCode = 400;
        String errorJson = """
                {
                    "error": "invalid_grant",
                    "error_description": "authorization code not found for code=INVALID_CODE",
                    "error_code": "KOE320"
                }
                """;

        given(httpManager.post(any(URI.class), any(), any()))
                .willThrow(new OAuthResponseException(statusCode, null, errorJson, "Server Error"));

        KakaoTokenRequest request = new KakaoTokenRequest.Builder(httpManager)
                .clientId("TEST_ID")
                .redirectUri("http://localhost:8080/oauth")
                .code("INVALID_CODE")
                .clientSecret("TEST_SECRET")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthResponseException.class)
                .satisfies(e -> {
                    OAuthResponseException ex = (OAuthResponseException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(400);
                    assertThat(ex.getErrorCode()).isEqualTo("KOE320");
                    assertThat(ex.getMessage()).contains("authorization code not found");
                });
    }

    @Test
    @DisplayName("카카오 토큰 요청 실패 - client_id 누락")
    void execute_Error() throws Exception {
        // given
        KakaoTokenRequest.Builder builder = new KakaoTokenRequest.Builder(httpManager)
                .redirectUri("http://localhost:8080/oauth")
                .code("INVALID_CODE")
                .clientSecret("TEST_SECRET");

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("client_id");
    }
}