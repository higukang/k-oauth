package kr.higu;

import kr.higu.exceptions.detailed.OAuthNetworkException;
import kr.higu.exceptions.detailed.OAuthResponseException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@Tag("integration")
class OAuthHttpManagerTest {

    private final OAuthHttpManager httpManager = OAuthHttpManager.getInstance();

    @Test
    @Disabled("Real-Network")
    @DisplayName("GET-httpbin")
    void get_RealNetwork_Success() throws Exception {
        // given
        URI uri = URI.create("https://httpbin.org/get?name=higu");
        Map<String, String> headers = Map.of("Accept", "application/json");

        // when
        String response = httpManager.get(uri, headers);

        // then
        assertThat(response).contains("\"name\": \"higu\"");
        assertThat(response).contains("\"Accept\": \"application/json\"");
    }

    @Test
    @Disabled("Real-Network")
    @DisplayName("POST-httpbin")
    void post_RealNetwork_Success() throws Exception {
        // given
        URI uri = URI.create("https://httpbin.org/post");
        String body = "grant_type=authorization_code&code=test_code";

        // when
        String response = httpManager.post(uri, null, body);

        // then
        assertThat(response).contains("test_code");
    }

    @Test
    @Disabled("Real-Network")
    @DisplayName("404 -> OAuthResponseException")
    void get_RealNetwork_404Error() {
        // given
        URI uri = URI.create("https://httpbin.org/status/404");

        // when, then
        assertThatThrownBy(() -> httpManager.get(uri, null))
                .isInstanceOf(OAuthResponseException.class)
                .satisfies(e -> {
                    OAuthResponseException ex = (OAuthResponseException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(404);
                });
    }

    @Test
    @Disabled("Real-Network")
    @DisplayName("wrong-domain -> OAuthNetworkException")
    void get_RealNetwork_NetworkError() {
        // given
        URI uri = URI.create("https://this-domain-should-not-exist-higu.com");

        // when, then
        assertThatThrownBy(() -> httpManager.get(uri, null))
                .isInstanceOf(OAuthNetworkException.class);
    }

    @Test
    @Disabled("Real-Network")
    @DisplayName("연결 타임아웃 테스트 - httpbin delay 활용")
    void get_Timeout_Test() {
        // given
        URI uri = URI.create("https://httpbin.org/delay/15");

        // when, then
        assertThatThrownBy(() -> httpManager.get(uri, null))
                .isInstanceOf(OAuthNetworkException.class);
    }
}