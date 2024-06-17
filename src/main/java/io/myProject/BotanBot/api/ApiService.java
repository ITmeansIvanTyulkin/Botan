package io.myProject.BotanBot.api;

import static io.myProject.BotanBot.api.MessageUtill.CONSTRUCTOR_EXCEPTION_MESSAGE;
import static java.lang.String.format;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ApiService {

  private ApiService() {
    throw new UnsupportedOperationException(
        format(CONSTRUCTOR_EXCEPTION_MESSAGE, ApiService.class));
  }

  public static ResponseSpecification responseSpecStatus200 = new ResponseSpecBuilder()
      .expectStatusCode(200)
      .build();
}
