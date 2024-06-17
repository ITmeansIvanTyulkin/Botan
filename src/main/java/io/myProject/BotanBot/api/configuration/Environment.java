package io.myProject.BotanBot.api.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Environment {

  @Value("${API_ENDPOINT}")
  private String endpoint;

  @Value("${API_URL}")
  private String url;

  @Value("${API_TOKEN}")
  private String token;

  public String getEndpoint() {
    return endpoint;
  }

  public String getUrl() {
    return url;
  }

  public String getToken() {
    return token;
  }

}
