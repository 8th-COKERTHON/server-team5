# 8th Cokerthon

해커톤 전에 미리 준비해두는 Spring Boot 백엔드 템플릿입니다.

## 포함된 것

- Spring Boot 3.5.3 / Java 17 / Gradle
- Spring Web, Validation, JPA, MySQL, Lombok
- Swagger UI
- Actuator health check
- MySQL Docker Compose
- local/prod/test profile
- 공통 응답, 공통 에러 응답, 전역 예외 처리
- 자체 회원가입/로그인, JWT Access Token 인증
- 샘플 API
- GitHub Actions CI

## 로컬 실행

```bash
cp .env.example .env
docker compose up -d
./gradlew bootRun
```

## 확인 URL

```text
Health:  http://localhost:8080/actuator/health
Swagger: http://localhost:8080/swagger-ui.html
Sample:  http://localhost:8080/api/samples
```

## 로그인 API

회원가입:

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"123456","nickname":"테스터"}'
```

로그인:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"123456"}'
```

인증이 필요한 API 호출:

```bash
curl http://localhost:8080/api/members/me \
  -H "Authorization: Bearer {accessToken}"
```

구현 범위는 해커톤용으로 단순하게 유지합니다.

- Access Token만 사용
- Refresh Token 없음
- 로그아웃 블랙리스트 없음
- 이메일 인증 없음
- 권한 세분화 없음

## 해커톤 전에 해두면 좋은 것

- [ ] 팀 이름/서비스 이름으로 `group`, `packageName`, Swagger title 바꾸기
- [ ] 발표 시나리오 기준으로 API 우선순위 정하기
- [ ] 도메인별 패키지 생성
- [ ] EC2 또는 배포 서버 준비
- [ ] DB 계정/비밀번호를 GitHub Secrets 또는 서버 `.env`에 등록
- [ ] 프론트와 Swagger URL 공유
- [ ] 로그인은 핵심 기능이 아니면 `X-USER-ID` 같은 임시 헤더로 합의

## 추천 패키지 구조

```text
domain/{domain}/controller
domain/{domain}/service
domain/{domain}/repository
domain/{domain}/entity
domain/{domain}/dto/request
domain/{domain}/dto/response
global/config
global/entity
global/exception
global/response
```

## 막혔을 때 원칙

- 발표 시나리오에 없는 기능은 과감히 제외합니다.
- DB가 막히면 Service에서 더미 데이터를 반환합니다.
- 복잡한 예외 처리보다 정상 흐름 완성을 우선합니다.
- 발표 2시간 전부터는 새 기능 추가를 멈추고 시연 플로우만 고칩니다.
