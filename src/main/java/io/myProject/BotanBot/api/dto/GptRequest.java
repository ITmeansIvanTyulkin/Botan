package io.myProject.BotanBot.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GptRequest {

  @JsonProperty("model")
  private String model;

  @JsonProperty("messages")
  private List<Messages> messages;

  @AllArgsConstructor
  @Getter
  @Builder
  @Setter
  public static class Messages {

    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;
  }

}
