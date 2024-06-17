package io.myProject.BotanBot.api;

import static java.lang.String.format;

/**
 * Класс для сообщений ошибок
 */
public final class MessageUtill {
  private MessageUtill() {
    throw new UnsupportedOperationException(
        format(CONSTRUCTOR_EXCEPTION_MESSAGE, MessageUtill.class));
  }
  public static final String CONSTRUCTOR_EXCEPTION_MESSAGE = "Создание экземпляра класса %s запрещено";

}
