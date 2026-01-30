package kr.higu.request;

import kr.higu.IHttpManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class AbstractRequestTest {

    @Mock
    private IHttpManager httpManager;

    static class TestRequest extends AbstractRequest<String> {
        private final URI baseUri;

        TestRequest(TestBuilder builder) {
            super(builder);
            this.baseUri = builder.uri;
        }

        @Override protected String getMethod() { return "GET"; }
        @Override protected URI getUri() { return baseUri; }
        @Override protected ErrorDetail parseError(String errorBody) { return null; }
    }

    static class TestBuilder extends AbstractRequest.Builder<String, TestBuilder> {
        URI uri;
        TestBuilder(IHttpManager hm, String url) {
            super(hm, String.class);
            this.uri = URI.create(url);
        }
        @Override protected TestBuilder self() { return this; }
        @Override public AbstractRequest<String> build() { return new TestRequest(this); }
    }

    @Test
    @DisplayName("파라미터 인코딩 - 한글 및 특수문자")
    void buildQueryParams_EncodingTest() throws Exception {
        // given
        given(httpManager.get(any(), any())).willReturn("\"ok\"");

        AbstractRequest<String> request = new TestBuilder(httpManager, "https://api.com")
                .addParam("name", "강희구")
                .addParam("param", "!@#")
                .build();

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);

        // when
        request.execute();

        // then
        verify(httpManager).get(uriCaptor.capture(), any());
        String finalUri = uriCaptor.getValue().toString();

        String encodedName = URLEncoder.encode("강희구", StandardCharsets.UTF_8);
        String encodedParam = URLEncoder.encode("!@#", StandardCharsets.UTF_8);

        assertThat(finalUri).contains(encodedName);
        assertThat(finalUri).contains(encodedParam);
    }

    @Test
    @DisplayName("URI 조립 테스트 - 쿼리 스트링 유무에 따른 커넥터(? vs &) 확인")
    void buildFinalUri_ConnectorTest() throws Exception {
        // given 1: 쿼리 스트링이 없는 URI
        given(httpManager.get(any(), any())).willReturn("\"success\"");
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);

        AbstractRequest<String> request1 = new TestBuilder(httpManager, "https://api.com")
                .addParam("p", "1")
                .build();

        // when 1
        request1.execute();

        // then 1:
        verify(httpManager).get(uriCaptor.capture(), any());
        assertThat(uriCaptor.getValue().toString()).isEqualTo("https://api.com?p=1");


        // given 2: 이미 쿼리 스트링이 있는 URI
        ArgumentCaptor<URI> uriCaptor2 = ArgumentCaptor.forClass(URI.class);
        AbstractRequest<String> request2 = new TestBuilder(httpManager, "https://api.com?base=true")
                .addParam("p", "1")
                .build();

        // when 2
        request2.execute();

        // then 2:
        verify(httpManager, atLeastOnce()).get(uriCaptor2.capture(), any());
        assertThat(uriCaptor2.getValue().toString()).isEqualTo("https://api.com?base=true&p=1");
    }
}