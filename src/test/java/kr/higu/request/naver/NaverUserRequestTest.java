package kr.higu.request.naver;

import kr.higu.IHttpManager;
import kr.higu.dto.naver.NaverUserResponse;
import kr.higu.exceptions.OAuthValidationException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NaverUserRequestTest {

    @Mock
    private IHttpManager httpManager;

    @Test
    @DisplayName("네이버 유저 정보 조회 성공 테스트")
    void execute_Success() throws Exception {
        // given
        String successJson = """
                {
                    "resultcode": "00",
                        "message": "success",
                        "response": {
                            "id": "examplerandomusersid",
                            "nickname": "higu",
                            "profile_image": "https://ssl.pstatic.net/static/pwe/address/img_profile.png",
                            "age": "20-29",
                            "gender": "M",
                            "email": "higu@example.com",
                            "name": "강희구",
                            "birthday": "09-13",
                            "birthyear": "2000"
                        }
                }
                """;

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> headerCaptor = ArgumentCaptor.forClass(Map.class);
        given(httpManager.get(any(URI.class), headerCaptor.capture())).willReturn(successJson);

        NaverUserRequest request = new NaverUserRequest.Builder(httpManager)
                .accessToken("ACCESS_TOKEN")
                .build();

        // when
        NaverUserResponse response = request.execute();

        // then
        assertThat(response.resultCode()).isEqualTo("00");
        assertThat(response.response().nickname()).isEqualTo("higu");

        Map<String, String> capturedHeaders = headerCaptor.getValue();
        assertThat(capturedHeaders).containsEntry("Authorization", "Bearer ACCESS_TOKEN");
    }

    @Test
    @DisplayName("네이버 API 에러 파싱 - resultcode/message 포맷")
    void execute_ApiError_Format1() throws Exception {
        // given
        String errorJson = """
                {
                    "resultcode": "024",
                    "message": "Authentication failed (인증 실패하였습니다.)"
                }
                """;
        given(httpManager.get(any(URI.class), any()))
                .willThrow(new OAuthResponseException(401, null, errorJson, "Unauthorized"));

        NaverUserRequest request = new NaverUserRequest.Builder(httpManager)
                .accessToken("INVALID_TOKEN")
                .build();

        // when, then
        assertThatThrownBy(request::execute)
                .isInstanceOf(OAuthResponseException.class)
                .satisfies(e -> {
                    OAuthResponseException ex = (OAuthResponseException) e;
                    assertThat(ex.getErrorCode()).isEqualTo("024");
                    assertThat(ex.getMessage()).contains("Authentication failed");
                });
    }

    @Test
    @DisplayName("빌드 실패 - 액세스 토큰 형식 오류")
    void build_Error_InvalidTokenFormat() {
        // given
        NaverUserRequest.Builder builder = new NaverUserRequest.Builder(httpManager);

        // when, then
        assertThatThrownBy(builder::build)
                .isInstanceOf(OAuthValidationException.class)
                .hasMessageContaining("A valid access token is required");
    }
}