package io.myProject.BotanBot.api;

import static io.myProject.BotanBot.api.ApiService.responseSpecStatus200;
import static io.myProject.BotanBot.api.MessageUtill.CONSTRUCTOR_EXCEPTION_MESSAGE;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;

import io.myProject.BotanBot.api.configuration.Environment;
import io.myProject.BotanBot.api.dto.GptRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GptService {

  private final Environment environment;

  @Autowired
  public GptService(Environment environment) {
    this.environment = environment;
  }

  public Response postChat(String model, String question) {
    GptRequest request = Builder.stepOneRequest(model, question);

    return given()
        .header("Authorization", "Bearer " + environment.getToken())
        .contentType(ContentType.JSON)
        .body(request)
        .log()
        .all()
        .when()
        .post(environment.getUrl() + environment.getEndpoint())
        .then()
        .spec(responseSpecStatus200)
        .log()
        .all()
        .extract()
        .response();
  }
}
