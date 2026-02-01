# K-OAuth
A simple Java OAuth 2.0 library for Korean OAuth providers (Kakao, Naver)

[![Release](https://jitpack.io/v/higukang/k-oauth.svg)](https://jitpack.io/#higukang/k-oauth)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

[Korean Version (한글 문서)](./README.ko.md)

## Table of Contents
1. **[Installation](#installation)**
2. **[General Usage](#general-usage)**
   - **[Kakao Client](#kakao-client)**
   - **[Naver Client](#naver-client)**
3. **[Advanced Features](#advanced-features)**
4. **[Error Handling](#error-handling)**
5. **[Inspiration](#inspiration)**

## Installation

### Gradle
Add the following to your `build.gradle` file:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.higukang:k-oauth:v1.0.0'
}
```

### IDE Integration (Optional)
If you cannot see the library's source code or Javadoc in your IDE:
- **IntelliJ**: Click **"Download Sources"** in the notification bar or use the Gradle tab to refresh with sources enabled.
- **VS Code**: Ensure the "Java Language Support" extension is installed; it typically handles sources automatically.

## General Usage

### Kakao Client

**1. Exchange Code for Access Token**
```java
KakaoClient kakaoClient = KakaoClient.create();

try {
    KakaoTokenResponse response = kakaoClient.getToken()
            .clientId("YOUR_REST_API_KEY")
            .redirectUri("YOUR_REDIRECT_URI")
            .code("AUTHORIZATION_CODE")
            .clientSecret("YOUR_CLIENT_SECRET") // Optional
            .build()
            .execute();

    System.out.println("Access Token: " + response.accessToken());
} catch (OAuthException e) {
    e.printStackTrace();
}
```
**2. Get User Profile**
```java
KakaoClient kakaoClient = KakaoClient.create();

try {
    KakaoUserResponse user = kakaoClient.getUserInfo()
            .accessToken("ACCESS_TOKEN")
            .secureResource(false)
            .propertyKeys(KakaoPropertyKey.EMAIL)
            .build()
            .execute();

    System.out.println("Nickname: " + user.kakaoAccount().profile().nickname());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

### Naver Client

**1. Exchange Code for Access Token**
```java
NaverClient naverClient = NaverClient.create();

try {
    NaverTokenResponse response = naverClient.getToken()
            .clientId("CLIENT_ID")
            .clientSecret("SECRET")
            .code("CODE")
            .state("STATE")
            .build()
            .execute();

    System.out.println("Access Token: " + response.accessToken());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

**2. Get User Profile**
```java
NaverClient naverClient = NaverClient.create();

try {
    NaverUserResponse response = new NaverUserRequest.Builder(httpManager)
            .accessToken("ACCESS_TOKEN")
            .build()
            .execute();
    
    System.out.println("Nickname: " + response.response().nickname());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

## Advanced Features
**Selective Property Retrieval (Kakao Only)**

You can request specific user properties to optimize the response size.

```java
KakaoUserResponse user = kakaoClient.getUserInfo()
        .accessToken("ACCESS_TOKEN")
        .propertyKeys(KakaoPropertyKey.EMAIL, KakaoPropertyKey.NICKNAME)
        .secureResource(true) // Return HTTPS URLs
        .build()
        .execute();
```

## Error Handling
**K-OAuth provides a detailed exception hierarchy to help you handle various failure scenarios.**

- OAuthValidationException: Thrown when mandatory parameters are missing before the request.

- OAuthResponseException: Thrown when the OAuth provider returns a non-2xx response or a logical error (Naver's 200 OK error).

- OAuthNetworkException: Thrown when network issues occur (timeouts, DNS failures).

```java
try {
    // execute request...
} catch (OAuthResponseException e) {
    System.out.println("Status Code: " + e.getStatusCode());
    System.out.println("Error Code: " + e.getErrorCode());
    System.out.println("Message: " + e.getMessage());
} catch (OAuthException e) {
    // Handle other exceptions
}

```
## Inspiration
| Inspiration |
|-------------|
| Inspired by [spotify-web-api-java](https://github.com/spotify-web-api-java/spotify-web-api-java).            |

## License
This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.