package io.myProject.BotanBot.api;

import static io.myProject.BotanBot.api.MessageUtill.CONSTRUCTOR_EXCEPTION_MESSAGE;
import static java.lang.String.format;

import io.myProject.BotanBot.api.dto.GptRequest;
import java.util.Arrays;

public class Builder {

  private Builder() {
    throw new UnsupportedOperationException(
        format(CONSTRUCTOR_EXCEPTION_MESSAGE, Builder.class));
  }

  public static GptRequest stepOneRequest(String model, String question) {
    return GptRequest.builder()
        .model(model)
        .messages(Arrays.asList(
            GptRequest.Messages.builder()
                .role("system")
                .content("You are a helpful assistant.")
                .build(),
            GptRequest.Messages.builder()
                .role("user")
                .content(question)
                .build()
        ))
        .build();
  }
}