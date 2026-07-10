package com.cotato.cokerthon.domain.sleep;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SleepJetlagIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void 목표보다_늦게_자는_경우_서쪽_방향_도시로_매칭된다() throws Exception {
		String accessToken = signupAndLogin("jetlag_west", "password1!", "웨스트");

		// 기획안 예시: 현재 03:00~10:00, 목표 23:00~07:00 → 시차 3시간30분, 뉴델리(WEST) 매칭
		String requestBody = """
			{
				"currentBedtime": "03:00",
				"currentWaketime": "10:00",
				"targetBedtime": "23:00",
				"targetWaketime": "07:00"
			}
			""";

		ResponseEntity<String> response = postJetlag(accessToken, requestBody);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode data = objectMapper.readTree(response.getBody()).path("data");
		assertThat(data.path("to").path("cityNameKr").asText()).isEqualTo("뉴델리");
		assertThat(data.path("jetlagMinutes").asInt()).isEqualTo(210);
		assertThat(data.path("jetlagLabel").asText()).isEqualTo("3시간 30분");
		assertThat(data.path("direction").asText()).isEqualTo("WEST");
		assertThat(data.path("currentSleep").path("sleepMinutes").asInt()).isEqualTo(420);
		assertThat(data.path("targetSleep").path("sleepMinutes").asInt()).isEqualTo(480);
		// 지구본 매핑용 위경도가 함께 내려오는지 확인 (서울 → 뉴델리)
		assertThat(data.path("from").path("latitude").asDouble()).isEqualTo(37.5665);
		assertThat(data.path("from").path("longitude").asDouble()).isEqualTo(126.9780);
		assertThat(data.path("to").path("latitude").asDouble()).isEqualTo(28.6139);
		assertThat(data.path("to").path("longitude").asDouble()).isEqualTo(77.2090);
	}

	@Test
	void 같은_시차_구간에_여러_도시가_있으면_랜덤으로_매칭된다() throws Exception {
		String accessToken = signupAndLogin("jetlag_tie", "password1!", "타이");

		// 현재 01:00~09:00(중간 05:00) vs 목표 00:00~08:00(중간 04:00) → 시차 1시간, WEST
		// 베이징/싱가포르가 같은 시차 구간(gap=60)에 매핑되어 있어 반복 호출 시 둘 다 나와야 한다
		String requestBody = """
			{
				"currentBedtime": "01:00",
				"currentWaketime": "09:00",
				"targetBedtime": "00:00",
				"targetWaketime": "08:00"
			}
			""";

		Set<String> matchedCities = new HashSet<>();
		for (int i = 0; i < 40; i++) {
			ResponseEntity<String> response = postJetlag(accessToken, requestBody);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			JsonNode data = objectMapper.readTree(response.getBody()).path("data");
			assertThat(data.path("direction").asText()).isEqualTo("WEST");
			assertThat(data.path("jetlagMinutes").asInt()).isEqualTo(60);
			matchedCities.add(data.path("to").path("cityNameKr").asText());
		}

		assertThat(matchedCities).containsExactlyInAnyOrder("베이징", "싱가포르");
	}

	@Test
	void 목표보다_일찍_자는_경우_동쪽_방향_도시로_매칭된다() throws Exception {
		String accessToken = signupAndLogin("jetlag_east", "password1!", "이스트");

		// 현재 22:00~06:00(중간 02:00) vs 목표 24:00~08:00(중간 04:00) → 시차 2시간, EAST
		String requestBody = """
			{
				"currentBedtime": "22:00",
				"currentWaketime": "06:00",
				"targetBedtime": "00:00",
				"targetWaketime": "08:00"
			}
			""";

		ResponseEntity<String> response = postJetlag(accessToken, requestBody);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode data = objectMapper.readTree(response.getBody()).path("data");
		assertThat(data.path("direction").asText()).isEqualTo("EAST");
		assertThat(data.path("jetlagMinutes").asInt()).isEqualTo(120);
		assertThat(data.path("to").path("cityNameKr").asText()).isEqualTo("호니아라");
	}

	@Test
	void 시차가_거의_없으면_서울로_매칭된다() throws Exception {
		String accessToken = signupAndLogin("jetlag_same", "password1!", "세임");

		String requestBody = """
			{
				"currentBedtime": "23:00",
				"currentWaketime": "07:00",
				"targetBedtime": "23:00",
				"targetWaketime": "07:00"
			}
			""";

		ResponseEntity<String> response = postJetlag(accessToken, requestBody);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode data = objectMapper.readTree(response.getBody()).path("data");
		assertThat(data.path("direction").asText()).isEqualTo("SAME");
		assertThat(data.path("to").path("cityNameKr").asText()).isEqualTo("서울");
	}

	private ResponseEntity<String> postJetlag(String accessToken, String requestBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);

		return restTemplate.postForEntity(
			"/api/sleep/jetlag",
			new HttpEntity<>(requestBody, headers),
			String.class
		);
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
