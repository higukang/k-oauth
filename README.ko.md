# K-OAuth
한국의 OAuth 인증(카카오, 네이버)을 위한 간단한 자바 OAuth 2.0 라이브러리입니다.

[![Release](https://jitpack.io/v/higukang/k-oauth.svg)](https://jitpack.io/#higukang/k-oauth)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

## 목차
1. **[설치 방법](#설치-방법)**
2. **[문서](#문서)**
3. **[기본 사용법](#기본-사용법)**
   - **[카카오 클라이언트](#카카오-클라이언트)**
   - **[네이버 클라이언트](#네이버-클라이언트)**
4. **[고급 기능](#고급-기능)**
5. **[에러 핸들링](#에러-핸들링)**
6. **[참고 및 영감](#참고-및-영감)**

---

## 설치 방법

### Gradle
`build.gradle` 파일에 아래 설정을 추가하세요:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.higukang:k-oauth:v1.0.0'
}
```

### IDE 연동 가이드 (선택 사항)
라이브러리를 프로젝트에 불러온 후, 인텔리제이 등 IDE에서 소스 코드나 주석(Javadoc)이 보이지 않는다면 아래 방법을 시도해 보세요.

- **IntelliJ**: 라이브러리 클래스 파일을 열었을 때 상단에 나타나는 "Download Sources" 알림줄을 클릭합니다.
또는, 오른쪽 Gradle 탭에서 "Reload All Gradle Projects" (코끼리 아이콘)를 클릭하여 다시 동기화합니다.


- **VS Code**: "Language Support for Java" 확장 프로그램이 설치되어 있다면 자동으로 소스 코드를 연결합니다.

## 문서
해당 라이브러리의 **[Javadoc](https://jitpack.io/com/github/higukang/k-oauth/1.0.0/javadoc/)** 을 확인할 수 있습니다

## 기본 사용법

### 카카오 클라이언트

**1. 인가 코드로 액세스 토큰 받기**
```java
KakaoClient kakaoClient = KakaoClient.create();

try {
     KakaoTokenResponse response = kakaoClient.getToken()
             .clientId("YOUR_REST_API_KEY")
             .redirectUri("YOUR_REDIRECT_URI")
             .code("AUTHORIZATION_CODE")
             .clientSecret("YOUR_CLIENT_SECRET") // 선택 사항
             .build()
             .execute();

    System.out.println("액세스 토큰: " + response.accessToken());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

**2. 사용자 정보 가져오기**
```java
KakaoClient kakaoClient = KakaoClient.create();

try {
     KakaoUserResponse user = kakaoClient.getUserInfo()
             .accessToken("ACCESS_TOKEN")
             .build()
             .execute();

    System.out.println("닉네임: " + user.kakaoAccount().profile().nickname());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

### 네이버 클라이언트

**1. 인가 코드로 액세스 토큰 받기**
```java
NaverClient naverClient = NaverClient.create();

try {
     NaverTokenResponse response = naverClient.getToken()
             .clientId("YOUR_CLIENT_ID")
             .clientSecret("YOUR_CLIENT_SECRET")
             .code("AUTHORIZATION_CODE")
             .state("YOUR_STATE")
             .build()
             .execute();

    System.out.println("액세스 토큰: " + response.accessToken());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

**2. 사용자 정보 가져오기**
```java
NaverClient naverClient = NaverClient.create();

try {
     NaverUserResponse response = naverClient.getUserInfo()
             .accessToken("ACCESS_TOKEN")
             .build()
             .execute();
    
    System.out.println("닉네임: " + response.response().nickname());
} catch (OAuthException e) {
    e.printStackTrace();
}
```
## 고급 기능
**선택적 프로퍼티 조회 (카카오 전용)**

필요한 사용자 정보만 선택하여 응답 데이터 크기를 최적화할 수 있습니다.

```java
KakaoUserResponse user = kakaoClient.getUserInfo()
        .accessToken("ACCESS_TOKEN")
        .propertyKeys(KakaoPropertyKey.EMAIL, KakaoPropertyKey.PHONE_NUMBER)
        .secureResource(true) // 이미지 URL을 HTTPS로 반환
        .build()
        .execute();
```

## 에러 핸들링
**K-OAuth는 다양한 실패 상황을 세밀하게 처리할 수 있도록 상세한 예외 계층 구조를 제공합니다.**

- OAuthValidationException: 요청 전 필수 파라미터가 누락되었을 때 발생합니다.

- OAuthResponseException: 제공자 서버가 에러를 반환하거나 논리적 에러(예: 네이버의 200 OK 에러 응답)가 발생했을 때 발생합니다.

- OAuthNetworkException: 타임아웃, DNS 오류 등 네트워크 문제가 발생했을 때 발생합니다.
```java
try {
    // 요청 실행...
} catch (OAuthResponseException e) {
    System.out.println("상태 코드: " + e.getStatusCode());
    System.out.println("에러 코드: " + e.getErrorCode());
    System.out.println("메시지: " + e.getMessage());
} catch (OAuthException e) {
    // 기타 예외 처리
}
```

## 참고 및 영감
| Inspiration |
|-------------|
| [spotify-web-api-java](https://github.com/spotify-web-api-java/spotify-web-api-java)의 설계를 참고하여 제작되었습니다.            |

## 라이선스
이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 [LICENSE](./LICENSE) 파일을 참조하세요.