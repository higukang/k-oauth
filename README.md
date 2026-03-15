# K-OAuth
A simple Java OAuth 2.0 library for Korean OAuth providers (Kakao, Naver)

[![Maven Central](https://img.shields.io/maven-central/v/kr.higu/k-oauth)](https://central.sonatype.com/artifact/kr.higu/k-oauth)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

[Korean Version (한글 버전 README)](./README.ko.md)

## Table of Contents
1. **[Installation](#installation)**
2. **[Documentation](#documentation)**
3. **[General Usage](#general-usage)**
   - **[Kakao Client](#kakao-client)**
   - **[Naver Client](#naver-client)**
4. **[Advanced Features](#advanced-features)**
5. **[Error Handling](#error-handling)**
6. **[Inspiration](#inspiration)**
7. **[Contributing](#contributing)**

## Installation

### Maven Central
Official releases are published to Maven Central.

#### Gradle
Add the following to your `build.gradle` file:

```gradle
dependencies {
    implementation 'kr.higu:k-oauth:0.1.1'
}
```

#### Maven
```xml
<dependency>
  <groupId>kr.higu</groupId>
  <artifactId>k-oauth</artifactId>
  <version>0.1.1</version>
</dependency>
```

### JitPack
JitPack remains available as a secondary channel for GitHub-based snapshot or branch builds.

#### Gradle
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.higukang:k-oauth:main-SNAPSHOT'
}
```

### IDE Integration (Optional)
If you cannot see the library's source code or Javadoc in your IDE:
- **IntelliJ**: Click **"Download Sources"** in the notification bar or use the Gradle tab to refresh with sources enabled.
- **VS Code**: Ensure the "Java Language Support" extension is installed; it typically handles sources automatically.

## Documentation
Javadoc is included in the published Maven Central artifacts through the `javadoc` classifier.

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
        .clientSecret("YOUR_CLIENT_SECRET") // Optional (null is ignored)
        .build()
        .execute();

    System.out.println("Access Token: " + response.accessToken());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

**2. Refresh Tokens with a Refresh Token**
```java
KakaoClient kakaoClient = KakaoClient.create();

try {
    KakaoRefreshTokenResponse response = kakaoClient.refreshToken()
            .clientId("YOUR_REST_API_KEY")
            .refreshToken("USER_REFRESH_TOKEN")
            .clientSecret("YOUR_CLIENT_SECRET") // Optional unless Client Secret is enabled
            .build()
            .execute();

    System.out.println("Refreshed Access Token: " + response.accessToken());
} catch (OAuthException e) {
    e.printStackTrace();
}
```

Kakao refresh token notes:
- If Client Secret is enabled in the Kakao console, `client_secret` must be included in the refresh request.
- `id_token` is only returned when the refresh token was originally issued with OpenID Connect.
- `refresh_token` and `refresh_token_expires_in` are only returned when the current refresh token has less than one month remaining.
- If Kakao returns an error such as `KOE237`, the library exposes it through `OAuthResponseException#getErrorCode()`.

**3. Get User Profile**
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
    NaverUserResponse response = naverClient.getUserInfo()
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
        .propertyKeys(KakaoPropertyKey.EMAIL, KakaoPropertyKey.PROFILE)
        .secureResource(true) // Return HTTPS URLs
        .build()
        .execute();
```

## Error Handling
**K-OAuth provides a detailed exception hierarchy to help you handle various failure scenarios.**

- **OAuthValidationException**: Thrown when mandatory parameters are missing before the request.

- **OAuthResponseException**: Thrown when the OAuth provider returns a non-2xx response or a logical error (Naver's 200 OK error).

- **OAuthNetworkException**: Thrown when network issues occur (timeouts, DNS failures).

- **OAuthParsingException**: Thrown when a provider response cannot be parsed as expected JSON.

Validation and optional parameter notes:
- Required values are validated at `build()` time and throw `OAuthValidationException`.
- Optional parameters with `null` values are safely ignored.

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

## Contributing
- Issues and Pull Requests are always welcome.  
- Feel free to contribute, from small typo fixes to feature suggestions.

## License
This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

<img referrerpolicy="no-referrer-when-downgrade" src="https://static.scarf.sh/a.png?x-pxid=ca8a4308-8bd3-43c2-b192-2a78d2b18c5c" />
