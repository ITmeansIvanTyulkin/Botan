package io.myProject.BotanBot;

import io.myProject.BotanBot.api.GptService;
import io.myProject.BotanBot.start.Botan;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest(classes = {Botan.class})
public class chatTest {

  private final GptService gptService;

  @Autowired
  public chatTest(GptService gptService) {
    this.gptService = gptService;
  }

  private String model = "gpt-3.5-turbo-0125";
  private String question = "Привет. Ты работаешь??";

  @Test
  public void checkResponse() {

    Response response = gptService.postChat(model, question);
    System.out.println(response);
  }


}
