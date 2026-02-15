package kr.higu.request.naver;

import kr.higu.IHttpManager;
import kr.higu.dto.naver.NaverTokenResponse;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.exceptions.detailed.OAuthParsingException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NaverTokenRequestTest {

    @Mock
    private IHttpManager httpManager;

    @Test
    @DisplayName("네이버 토큰 요청 성공 테스트")
    void execute_Success() throws Exception {
        // given
        String successJson = """
                {
                    "access_token": "AAAAO123456789",
                    "refresh_token": "BBBB987654321",
                    "token_type": "bearer",
                    "expires_in": "3600"
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(successJson);

        NaverTokenRequest request = new NaverTokenRequest.Builder(httpManager)
                .clientId("CLIENT_ID")
                .clientSecret("SECRET")
                .code("CODE")
                .state("STATE")
                .build();

        // when
        NaverTokenResponse response = request.execute();

        // then
        assertThat(response.accessToken()).isEqualTo("AAAAO123456789");
        assertThat(response.expiresIn()).isEqualTo("3600");
        assertThat(response.tokenType()).isEqualTo("bearer");
    }

    @Test
    @DisplayName("200 OK인데 바디에 에러가 있는 경우")
    void execute_Error_With_200_Ok() throws Exception {
        // given
        // 네이버는 인증 실패 시에도 HTTP 200,  바디에 error
        String errorJson = """
                {
                    "error": "invalid_request",
                    "error_description": "no valid data in session"
                }
                """;
        given(httpManager.post(any(URI.class), any(), any())).willReturn(errorJson);

        NaverTokenRequest request = new NaverTokenRequest.Builder(httpManager)
                .clientId("ID")
                .clientSecret("SECRET")
                .code("CODE")
                .state("STATE")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthResponseException.class)
                .satisfies(e -> {
                    OAuthResponseException ex = (OAuthResponseException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(200);
                    assertThat(ex.getErrorCode()).isEqualTo("invalid_request");
                    assertThat(ex.getMessage()).contains("no valid data in session");
                });
    }

    @Test
    @DisplayName("필수 파라미터(state) 누락 시 빌드 에러")
    void build_Error_When_State_Is_Missing() {
        // given
        NaverTokenRequest.Builder builder = new NaverTokenRequest.Builder(httpManager)
                .clientId("ID")
                .clientSecret("SECRET")
                .code("CODE");
        // .state("STATE")

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("state");
    }

    @Test
    @DisplayName("200 OK인데 malformed JSON이면 OAuthParsingException")
    void execute_Error_With_200_Ok_MalformedJson() throws Exception {
        // given
        given(httpManager.post(any(URI.class), any(), any())).willReturn("not-json");

        NaverTokenRequest request = new NaverTokenRequest.Builder(httpManager)
                .clientId("ID")
                .clientSecret("SECRET")
                .code("CODE")
                .state("STATE")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthParsingException.class)
                .hasMessageContaining("Failed to parse NaverTokenResponse response");
    }
}