package com.cotato.cokerthon.domain.companion;

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
class CompanionIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void 아이디로_동행자를_검색한다() throws Exception {
		signupAndLogin("comp_search_me", "password1!", "나");
		signupAndLogin("comp_search_target", "password1!", "민주");
		String accessToken = login("comp_search_me", "password1!");

		ResponseEntity<String> response = search(accessToken, "comp_search_target");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode data = objectMapper.readTree(response.getBody()).path("data");
		assertThat(data.path("loginId").asText()).isEqualTo("comp_search_target");
		assertThat(data.path("nickname").asText()).isEqualTo("민주");
		assertThat(data.path("alreadyCompanion").asBoolean()).isFalse();
	}

	@Test
	void 동행자를_추가하면_즉시_목록에_나타난다() throws Exception {
		String accessToken = signupAndLogin("comp_add_me", "password1!", "나");
		signupAndLogin("comp_add_target", "password1!", "민지");

		ResponseEntity<String> addResponse = add(accessToken, "comp_add_target");
		assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode addedData = objectMapper.readTree(addResponse.getBody()).path("data");
		assertThat(addedData.path("nickname").asText()).isEqualTo("민지");
		// 아직 수면시차 계산 기록이 없으므로 도시 정보는 null
		assertThat(addedData.path("city").isNull()).isTrue();

		ResponseEntity<String> listResponse = getCompanions(accessToken);
		assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonNode list = objectMapper.readTree(listResponse.getBody()).path("data");
		assertThat(list).hasSize(1);
		assertThat(list.get(0).path("nickname").asText()).isEqualTo("민지");
	}

	@Test
	void 동행자의_수면시차_계산_기록이_있으면_카드에_함께_내려온다() throws Exception {
		String myToken = signupAndLogin("comp_city_me", "password1!", "나");
		String friendToken = signupAndLogin("comp_city_friend", "password1!", "민지");

		// 기획안 예시와 동일한 입력 (03:00~10:00 / 23:00~07:00) → 뉴델리, 3시간30분, WEST
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
		jetlagHeaders.setBearerAuth(friendToken);
		restTemplate.postForEntity("/api/sleep/jetlag", new HttpEntity<>(jetlagBody, jetlagHeaders), String.class);

		add(myToken, "comp_city_friend");

		ResponseEntity<String> listResponse = getCompanions(myToken);
		JsonNode companion = objectMapper.readTree(listResponse.getBody()).path("data").get(0);

		assertThat(companion.path("city").path("cityNameKr").asText()).isEqualTo("뉴델리");
		assertThat(companion.path("city").path("latitude").asDouble()).isEqualTo(28.6139);
		assertThat(companion.path("city").path("longitude").asDouble()).isEqualTo(77.2090);
		assertThat(companion.path("jetlagLabel").asText()).isEqualTo("3시간 30분");
		assertThat(companion.path("direction").asText()).isEqualTo("WEST");
		assertThat(companion.path("lastRecordedAt").isNull()).isFalse();
	}

	@Test
	void 자기_자신은_동행자로_추가할_수_없다() throws Exception {
		String accessToken = signupAndLogin("comp_self", "password1!", "나");

		ResponseEntity<String> response = add(accessToken, "comp_self");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void 같은_동행자를_중복으로_추가할_수_없다() throws Exception {
		String accessToken = signupAndLogin("comp_dup_me", "password1!", "나");
		signupAndLogin("comp_dup_target", "password1!", "민지");

		add(accessToken, "comp_dup_target");
		ResponseEntity<String> secondAdd = add(accessToken, "comp_dup_target");

		assertThat(secondAdd.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void 동행자를_삭제하면_목록에서_사라진다() throws Exception {
		String accessToken = signupAndLogin("comp_del_me", "password1!", "나");
		signupAndLogin("comp_del_target", "password1!", "민지");

		ResponseEntity<String> addResponse = add(accessToken, "comp_del_target");
		Long companionMemberId = objectMapper.readTree(addResponse.getBody())
			.path("data").path("companionMemberId").asLong();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		ResponseEntity<String> deleteResponse = restTemplate.exchange(
			"/api/companions/" + companionMemberId, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		JsonNode list = objectMapper.readTree(getCompanions(accessToken).getBody()).path("data");
		assertThat(list).isEmpty();
	}

	private ResponseEntity<String> search(String accessToken, String targetLoginId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		return restTemplate.exchange(
			"/api/companions/search?loginId=" + targetLoginId, HttpMethod.GET, new HttpEntity<>(headers),
			String.class);
	}

	private ResponseEntity<String> add(String accessToken, String targetLoginId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		String body = """
			{"loginId": "%s"}
			""".formatted(targetLoginId);
		return restTemplate.postForEntity("/api/companions", new HttpEntity<>(body, headers), String.class);
	}

	private ResponseEntity<String> getCompanions(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		return restTemplate.exchange("/api/companions", HttpMethod.GET, new HttpEntity<>(headers), String.class);
	}

	private String signupAndLogin(String loginId, String password, String nickname) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String signupBody = """
			{"id": "%s", "password": "%s", "nickname": "%s"}
			""".formatted(loginId, password, nickname);
		restTemplate.postForEntity("/api/auth/signup", new HttpEntity<>(signupBody, headers), String.class);

		return login(loginId, password);
	}

	private String login(String loginId, String password) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String loginBody = """
			{"id": "%s", "password": "%s"}
			""".formatted(loginId, password);
		ResponseEntity<String> loginResponse = restTemplate.postForEntity(
			"/api/auth/login", new HttpEntity<>(loginBody, headers), String.class);

		JsonNode data = objectMapper.readTree(loginResponse.getBody()).path("data");
		return data.path("accessToken").asText();
	}
}
