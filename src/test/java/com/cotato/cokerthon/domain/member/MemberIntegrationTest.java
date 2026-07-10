package com.cotato.cokerthon.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void 수면시차_계산_전에는_서울이_기본_위치로_내려온다() throws Exception {
		String accessToken = signupAndLogin("member_me_before", "password1!", "채은");

		ResponseEntity<String> response = getMyInfo(accessToken);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode data = objectMapper.readTree(response.getBody()).path("data");
		assertThat(data.path("nickname").asText()).isEqualTo("채은");
		assertThat(data.path("city").path("cityNameKr").asText()).isEqualTo("서울");
		assertThat(data.path("city").path("latitude").asDouble()).isEqualTo(37.5665);
		assertThat(data.path("city").path("longitude").asDouble()).isEqualTo(126.9780);
		assertThat(data.path("jetlagMinutes").asInt()).isEqualTo(0);
		assertThat(data.path("direction").asText()).isEqualTo("SAME");
		assertThat(data.path("lastRecordedAt").isNull()).isTrue();
	}

	@Test
	void 수면시차_계산_후에는_현재_위치의_위경도가_내려온다() throws Exception {
		String accessToken = signupAndLogin("member_me_after", "password1!", "채은");

		// 기획안 예시 (03:00~10:00 / 23:00~07:00) → WEST(gap=210: 뉴델리/콜롬보 tie)
		String jetlagBody = """
			{
				"currentBedtime": "03:00",
				"currentWaketime": "10:00",
				"targetBedtime": "23:00",
				"targetWaketime": "07:00"
			}
			""";
		HttpHeaders jetlagHeaders = new HttpHeaders();
		jetlagHeaders.setContentType(MediaType.APPLICATION_JSON);
		jetlagHeaders.setBearerAuth(accessToken);
		restTemplate.postForEntity("/api/sleep/jetlag", new HttpEntity<>(jetlagBody, jetlagHeaders), String.class);

		ResponseEntity<String> response = getMyInfo(accessToken);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode data = objectMapper.readTree(response.getBody()).path("data");
		assertThat(data.path("city").path("cityNameKr").asText()).isIn("뉴델리", "콜롬보");
		assertThat(data.path("city").path("latitude").asDouble()).isNotZero();
		assertThat(data.path("city").path("longitude").asDouble()).isNotZero();
		assertThat(data.path("jetlagLabel").asText()).isEqualTo("3시간 30분");
		assertThat(data.path("direction").asText()).isEqualTo("WEST");
	}

	private ResponseEntity<String> getMyInfo(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		return restTemplate.exchange("/api/members/me", HttpMethod.GET, new HttpEntity<>(headers), String.class);
	}

	private String signupAndLogin(String loginId, String password, String nickname) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String signupBody = """
			{"id": "%s", "password": "%s", "nickname": "%s"}
			""".formatted(loginId, password, nickname);
		restTemplate.postForEntity("/api/auth/signup", new HttpEntity<>(signupBody, headers), String.class);

		String loginBody = """
			{"id": "%s", "password": "%s"}
			""".formatted(loginId, password);
		ResponseEntity<String> loginResponse = restTemplate.postForEntity(
			"/api/auth/login", new HttpEntity<>(loginBody, headers), String.class);

		JsonNode data = objectMapper.readTree(loginResponse.getBody()).path("data");
		return data.path("accessToken").asText();
	}
}
