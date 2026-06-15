# API Gateway Service

이 프로젝트는 레슨 매칭 플랫폼(Lesson Matching Platform)의 진입점 역할을 하는 **Spring Cloud Gateway** 서비스입니다. 모든 클라이언트 요청은 이 게이트웨이를 거쳐 내부 마이크로서비스로 라우팅되며, 공통적인 인증 및 인가 처리를 담당합니다.

## 🛠️ 기술 스택 (Tech Stack)
- **Java**: 21
- **Framework**: Spring Boot 4.1.0, Spring Cloud 2025.1.2
- **Gateway**: Spring Cloud Gateway (WebFlux 기반)
- **Security**: Spring Security WebFlux, JWT (JSON Web Token)
- **Build Tool**: Gradle

## ✨ 주요 기능 (Features)

### 1. API 라우팅 (Routing)
`application.yml`에 정의된 라우팅 규칙에 따라 클라이언트의 요청을 적절한 백엔드 마이크로서비스로 포워딩합니다.
- `/auth/**` : 인증 서비스(`AUTH_SERVICE_HOST`)로 라우팅
- `/get/**` : 목업 서버(`MOCK_SERER`)로 라우팅
- 요청을 전달할 때 게이트웨이를 거쳤음을 알리기 위해 `X-Request-Source: Gateway` 헤더를 추가합니다.

### 2. 보안 및 인증 (Security & Authentication)
- **JWT 기반 인증**: 클라이언트로부터 전달받은 JWT 토큰의 유효성을 검사하여 인증을 수행합니다 (`JwtAuthenticationManager`, `JwtAuthenticationConverter`).
- **권한 설정**: 
  - `/auth/**`, `/public/**` 엔드포인트는 인증 없이 접근할 수 있습니다. (Permit All)
  - 그 외의 모든 요청은 인증을 거쳐야 합니다.
- WebFlux 전용 Security 설정(`@EnableWebFluxSecurity`)을 사용하며, 무상태(Stateless) API 통신을 위해 CSRF, Form Login, HTTP Basic 기능은 비활성화되어 있습니다.

### 3. 사용자 정보 전달 필터 (UserInfoFilter)
- 인가된 사용자의 요청이 게이트웨이를 통과할 때 동작하는 전역 필터(Global Filter)입니다.
- SecurityContext에서 인증 객체(JWT 파싱 결과)를 가져와 `userId`와 `role` 정보를 추출합니다.
- 하위 마이크로서비스로 요청을 전달할 때, **`X-User-Id`** 및 **`X-User-Role`** HTTP 헤더에 해당 정보를 담아 전달합니다.
- 이를 통해 개별 마이크로서비스는 별도의 JWT 검증 과정 없이 헤더 정보만으로 요청한 사용자를 식별할 수 있습니다.

## 📂 프로젝트 구조 (Project Structure)
```text
src/main/java/com/hwan/gateway/
 ├── config/
 │    └── SecurityConfig.java         # Spring Security WebFlux 설정 (경로 권한 및 필터 등록)
 ├── controller/
 │    └── AuthController.java         # 게이트웨이 레벨의 인증 관련 컨트롤러
 ├── filter/
 │    └── UserInfoFilter.java         # 인증된 사용자 정보를 헤더에 주입하는 Global Filter
 ├── jwt/
 │    ├── JwtAuthenticationConverter.java # HTTP 요청에서 JWT 토큰을 추출/변환
 │    ├── JwtAuthenticationManager.java   # JWT 토큰 검증 및 Authentication 객체 생성
 │    └── JwtProvider.java            # JWT 토큰 생성 및 파싱 유틸리티
 └── GatewayApplication.java          # 메인 애플리케이션 클래스
```

## ⚙️ 주요 설정 (application.yml)
- 포트: `8080`
- 환경 변수 및 Secret Manager 연동 (`import: sm://`)
- 라우팅 및 Predicate, Filter 설정
- JWT 시크릿 키 설정 (`jwt.secret`)
