# 8th Cokerthon Team 5 Backend

수면 패턴을 시차로 해석해 현재 나의 수면 국가를 보여주고, 목표 수면 시간으로 돌아오는 귀국 루트를 안내하는 Spring Boot 백엔드입니다.

## 기술 스택

- Java 17
- Spring Boot 3.5.3
- Spring Web, Spring Security, Spring Validation
- Spring Data JPA, MySQL
- JWT Access Token 인증
- Swagger UI / Springdoc OpenAPI
- Docker, Docker Compose
- GitHub Actions CI/CD

## 주요 기능

- 회원가입/로그인
  - 아이디, 비밀번호, 닉네임 기반 회원가입
  - BCrypt 비밀번호 암호화
  - JWT Access Token 발급
- MVP1: 나의 수면 국가 확인
  - 현재 취침/기상 시간과 목표 취침/기상 시간을 입력받아 수면시차 계산
  - 서울 기준 시차에 맞는 도시를 매칭해 보딩패스 형태로 반환
- MVP2: 친구와 수면 국가 공유
  - 로그인 아이디로 동행자 검색
  - 동행자 추가, 목록 조회, 삭제
  - 동행자의 최근 수면 도시와 수면시차 조회
- MVP3: 귀국 루트
  - 수면시차 계산 결과를 기준으로 목표 수면 시간까지의 단계별 귀국 루트 생성
  - 현재 루트, 오늘의 항공권, 취침 후 다음 경유지 도착 처리

## 배포 및 문서

- FE: https://client-team5-git-main-minseoeum0114-5218s-projects.vercel.app
- BE Health: https://13.125.171.232.nip.io/actuator/health
- Swagger: https://13.125.171.232.nip.io/swagger-ui.html
- OpenAPI JSON: https://13.125.171.232.nip.io/v3/api-docs

## 가점 요소

| 항목 | 구현 내용 | 가점 |
| --- | --- | --- |
| CI/CD 구축 | GitHub Actions로 테스트, Docker 이미지 빌드/푸시, EC2 배포 자동화 | 1점 |
| HTTPS 설정 | `https://13.125.171.232.nip.io`로 BE HTTPS 접근 가능 | 2점 |
| 로그인 구현 | 회원가입/로그인, JWT Access Token 인증 구현 | 1점 |
| 비밀번호 암호화 | BCrypt로 비밀번호 암호화 저장 | 로그인 가점 조건 충족 |

## 로컬 실행

```bash
cp .env.example .env
docker compose up -d
./gradlew bootRun
```

로컬 확인 URL:

```text
Health:  http://localhost:8080/actuator/health
Swagger: http://localhost:8080/swagger-ui.html
```

## 환경 변수

`.env.example`을 복사해 `.env`를 만든 뒤 로컬 환경에 맞게 수정합니다.

```text
DB_NAME=cokerthon
DB_USERNAME=cokerthon
DB_PASSWORD=cokerthon
MYSQL_ROOT_PASSWORD=cokerthon-root
DB_HOST_PORT=3306
JWT_SECRET=change-me
JWT_ACCESS_TOKEN_EXPIRATION_MILLIS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
APP_SWAGGER_SERVER_URL=http://localhost:8080
```

운영 환경에서는 `JWT_SECRET`, DB 비밀번호, Docker Hub 토큰, EC2 SSH 키를 GitHub Secrets 또는 서버 `.env`로 관리합니다.

## API 사용 예시

### 회원가입

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"id":"testuser","password":"password123!","nickname":"테스터"}'
```

### 로그인

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"id":"testuser","password":"password123!"}'
```

응답은 공통 래퍼 형태이며, `data.accessToken`을 이후 요청에 사용합니다.

```bash
curl http://localhost:8080/api/members/me \
  -H "Authorization: Bearer {accessToken}"
```

### 수면시차 계산

```bash
curl -X POST http://localhost:8080/api/sleep/jetlag \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {accessToken}" \
  -d '{
    "currentBedtime": "03:00",
    "currentWaketime": "10:00",
    "targetBedtime": "23:00",
    "targetWaketime": "07:00"
  }'
```

### 귀국 루트 생성

```bash
curl -X POST http://localhost:8080/api/return-routes/results/{resultId} \
  -H "Authorization: Bearer {accessToken}"
```

### 동행자 추가

```bash
curl -X POST http://localhost:8080/api/companions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {accessToken}" \
  -d '{"loginId":"frienduser"}'
```

자세한 요청/응답 스키마는 Swagger에서 확인할 수 있습니다.

## 프로젝트 구조

```text
src/main/java/com/cotato/cokerthon
├── domain
│   ├── auth
│   ├── city
│   ├── companion
│   ├── member
│   ├── route
│   ├── sample
│   └── sleep
└── global
    ├── config
    ├── entity
    ├── exception
    ├── response
    └── security
```

각 도메인은 `controller`, `service`, `repository`, `entity`, `dto/request`, `dto/response` 단위로 나누어 관리합니다.

## BE 역할 분담

| 담당 영역 | 담당자 | 상태 |
| --- | --- | --- |
| MVP1: 나의 수면 국가 확인 | 해민 | 완료 |
| MVP2: 친구와 수면 국가 공유 | 해민 | 완료 |
| MVP3: 귀국 루트 | 정원 | 완료 |
| 로그인/회원가입 | 정원 | 완료 |
| 배포 | 정원 | 완료 |

## 테스트

```bash
./gradlew test --no-daemon
```

GitHub Actions는 `main`, `develop` 브랜치 push와 pull request에서 테스트를 실행합니다. `develop` push 시 Docker 이미지를 빌드하고 EC2에 배포합니다.
