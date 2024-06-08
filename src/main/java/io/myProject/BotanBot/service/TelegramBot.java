package io.myProject.BotanBot.service;

import io.myProject.BotanBot.bottalking.BotTalking;
import io.myProject.BotanBot.config.BotConfig;
import io.myProject.BotanBot.waitings.Waitings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot implements Waitings {

    public static final String ANSWER_TO_USER = "Ответ пользователю: ";
    final BotConfig config;
    public static final Map<Long, List<String>> userState = new HashMap<>();
    boolean isSearchCommandPressed = false;
    private static final Logger LOGGER = Logger.getLogger(TelegramBot.class.getName());

    BotTalking botTalking = new BotTalking();

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начало работы"));
        listOfCommands.add(new BotCommand("/help", "Как пользоваться этим ботом"));
        listOfCommands.add(new BotCommand("/search", "Найти недвижимость!"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            LOGGER.info("Ошибка настройки команд бота " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/help":
                    helpCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/search":
                    searchCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "Личный кабинет":
                    sendMessage(chatId, "Вы выбрали 'Личный кабинет'.");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Личный кабинет");
                    sendParameterOfTheNewMenuMoscow(chatId);
                    break;

                case "Возможности бота":
                    sendMessage(chatId, "Вы выбрали 'Возможности бота'.");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Возможности бота");
                    sendParameterOfTheNewMenuPiter(chatId);
                    break;

                case "Оформить подписку":
                    sendMessage(chatId, "Вы выбрали 'Оформить подписку'.");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Оформить подписку");
                    sendParameterOfTheNewMenuSochi(chatId);
                    break;

                case "В рамках МКАДа":
                    sendMessage(chatId, "Вы выбрали объект недвижимости в рамках МКАДа!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("В рамках МКАДа");
                    sendParameterMenu(chatId);
                    break; // TODO оставить для других кнопок.

                case "За МКАДом":
                    sendMessage(chatId, "Вы выбрали объект недвижимости за МКАДом!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("За МКАДом");
                    sendParameterMenu(chatId);
                    break; // TODO оставить для других кнопок.

                case "\u2699 Настроить поиск":
                    sendMessage(chatId, "Отлично! Давайте настроим поиск по Вашим параметрам!");
                    try {
                        Thread.sleep(Waitings.WAIT_FOR_3_SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // В чате появилась кнопка для перехода на сайт для детальной настройки поиска
                    sendTheButtonToCustomize(chatId);
                    try {
                        Thread.sleep(WAIT_FOR_7_SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        sendAMessageToSeeVariants(chatId);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break; // TODO оставить для других кнопок.

                case "\u2936 Начать поиск заново!":
                    sendMessage(chatId, "Окей, не страшно, начинаем заново :)");
                    searchCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break; // TODO оставить для других кнопок.

                case "Комната":
                    sendMessage(chatId, "Вы выбрали аренду комнаты в квартире! Помимо Вас в квартире будут проживать ещё и другие арендаторы. Однако, это самый выгодный по цене вариант. Обычно именно с аренды комнаты в квартире начинают свою жизнь приезжие в большом городе. Позже Вы сможете переехать из комнаты в студию или квартиру, или даже дом!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Комната");
                    try {
                        Thread.sleep(WAIT_FOR_5_SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAIT_FOR_5_SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break; // TODO оставить для других кнопок.

                case "1 человек":
                    sendMessage(chatId, "Записал Ваш ответ: Вы будете проживать один(-а), хорошо. ");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Проживать будет 1 человек.");
                    try {
                        Thread.sleep(WAIT_FOR_3_SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAIT_FOR_5_SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIAmReadyToRepeatYourChoice(chatId);
                    break; // TODO оставить для других кнопок.

                case "Посмотреть варианты":
                    sendMessage(chatId, "Отлично, смотрим!");
                    sendPaymentImage(chatId);
                    break; // TODO оставить для других кнопок.

                case "Начать поиск заново!":
                    sendMessage(chatId, "Отлично, ищем заново!");
                    searchCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break; // TODO оставить для других кнопок.

                default:
                    sendMessageWithButtonChooseTheCity(chatId, "Я не знаю, что на это ответить... Напишите подробно Ваш вопрос, а я поищу свободного оператора, который сможет Вам помочь!");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String prompt = botTalking.loadMessage("greetings");
        String answer1 = "Привет, " + name + prompt;
        LOGGER.info(ANSWER_TO_USER + name);
        sendMessageWithButtonChooseTheCity(chatId, answer1);
    }

    private void searchCommandReceived(long chatId, String name) {
        String prompt = botTalking.loadMessage("intro");
        String answer3 = "Итак, " + name + prompt;
        LOGGER.info(ANSWER_TO_USER + name);
        sendMessageWithButtonChooseTheCity(chatId, answer3);
    }

    private void helpCommandReceived(long chatId, String name) {
        String prompt = botTalking.loadMessage("info");
        String answer4 = name + prompt;
        LOGGER.info(ANSWER_TO_USER + name);
        sendMessageWithButtonChooseTheCity(chatId, answer4);
    }

    private void sendMessageWithButtonChooseTheCity(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyMarkup);
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Москва");
        row.add("Питер");
        row.add("Сочи");
        keyboard.add(row);
        replyMarkup.setKeyboard(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.info("Ошибка " + e.getMessage());
        }
    }

    private void sendParameterOfTheNewMenuMoscow(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("А сейчас выберите, пожалуйста, в какой локации:");
        ReplyKeyboardMarkup replyMarkup = newMenuMoscow();
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup newMenuMoscow() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("В рамках МКАДа");
        row1.add("За МКАДом");
        keyboard.add(row1);

        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    private void sendParameterOfTheNewMenuPiter(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("А сейчас выберите, пожалуйста, в какой локации:");
        ReplyKeyboardMarkup replyMarkup = newMenuPiter();
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup newMenuPiter() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("В рамках ЗСД");
        row1.add("За ЗСД");
        row1.add("Ленобласть");
        keyboard.add(row1);

        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    private void sendParameterOfTheNewMenuSochi(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("А сейчас выберите, пожалуйста, в какой локации:");
        ReplyKeyboardMarkup replyMarkup = newMenuSochi();
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup newMenuSochi() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Адлер");
        row1.add("Лазоревское");
        row1.add("город Сочи");
        keyboard.add(row1);

        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.info("Ошибка " + e.getMessage());
        }
    }

    private void sendParameterMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("А сейчас выберите, пожалуйста, желаемый вариант недвижимости для аренды:");
        ReplyKeyboardMarkup replyMarkup = createParameterMenuKeyboard();
        // Устанавливаем значение 'true', чтобы скрыть кнопки после выбора.
        replyMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createParameterMenuKeyboard() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Комната");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Студия");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("1-комнатная");
        keyboard.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        row4.add("2-комнатная");
        keyboard.add(row4);

        KeyboardRow row5 = new KeyboardRow();
        row5.add("3-комнатная");
        keyboard.add(row5);

        KeyboardRow row6 = new KeyboardRow();
        row6.add("Дом");
        //row6.add("Назад");  // <-- Добавление кнопки "Назад"
        keyboard.add(row6);

        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    private void sendParameterPeople(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("А сейчас выберите, пожалуйста, сколько человек будет там проживать:");
        ReplyKeyboardMarkup replyMarkup = createParameterMenuPeople();
        // Устанавливаем значение 'true', чтобы скрыть кнопки после выбора.
        replyMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createParameterMenuPeople() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("1 человек");
        row1.add("2 человека");


        KeyboardRow row2 = new KeyboardRow();
        row2.add("3 человека");
        row2.add("4 человека");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("5 человек");
        row3.add("Более 5 человек");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    private void sendAMessageThatIDoYourReplies(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Обрабатываю все Ваши ответы!");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAMessageToSeeVariants(long chatId) throws InterruptedException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        Thread.sleep(WAIT_FOR_5_SEC);
        message.setText("Смотрите, что я нашёл! Нажмите кнопку ниже - 'Посмотреть варианты'");
        ReplyKeyboardMarkup replyMarkup = seeTheVariantsButton();
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup seeTheVariantsButton() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Посмотреть варианты");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Начать поиск заново!");
        keyboard.add(row2);

        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    private void sendAMessageThatIAmReadyToRepeatYourChoice(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String parameters = "<b>" + userState.get(chatId).toString().toUpperCase() + "</b>";
        message.setText("Отлично, я записал Ваши пожелания! Я буду искать для Вас недвижимость по параметрам: \n\n" + parameters + "\n\nТеперь давайте настроим поиск по цене аренды, выбору района, этажу и желаемой площади квартиры. Для этого нажмите кнопку  <b> 'Настроить поиск' </b>. Если нужен поиск по другим параметрам, нажмите кнопку <b> 'Начать поиск заново!' </b>");
        message.setParseMode(ParseMode.HTML);

        ReplyKeyboardMarkup replyMarkup = confirmTheChoice();
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup confirmTheChoice() {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("\u2699 Настроить поиск");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("\u2936 Начать поиск заново!");
        keyboard.add(row2);

        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    // Вывод кнопки настроек параметров поиска (сайт).
    private void sendTheButtonToCustomize(long chatId) {
        sendParametersButton(chatId);
    }

    private void sendParametersButton(long chatId) {
        String parameters = userState.get(chatId).toString().toUpperCase();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Настроить параметры");

        if (parameters.contains("МОСКВА")) {
            button.setUrl("https://botan-rent.ru/Moscow/");
        } else if (parameters.contains("САНКТ-ПЕТЕРБУРГ")) {
            button.setUrl("https://botan-rent.ru/Piter/");
        } else if (parameters.contains("СОЧИ")) {
            button.setUrl("https://botan-rent.ru/Sochi/");
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(List.of(button)));

        // Создаем объект для отправки сообщения
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Для точной настройки поиска, нажмите, пожалуйста, кнопку ниже:");
        message.setReplyMarkup(markup);

        // Очищаю массив данных от пользователя
        userState.get(chatId).clear();

        try {
            // Отправляем сообщение
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Блок оплаты.
    private void sendPaymentImage(long chatId) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(String.valueOf(chatId));
        photo.setCaption(TEXT + MAKE_A_USER_TO_PAY + "\n\n" + TELEGRAM_POLICY);

        try {
            photo.setPhoto(new InputFile(new URL("https://i.imgur.com/DofFqma.png").openStream(), "image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendPayButton(chatId);
    }

    private void sendPayButton(long chatId) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Оплатить");
        button.setUrl("https://example.com/payment");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(List.of(button)));

        // Создаем объект для отправки сообщения
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Для перехода на сайт оплаты нажмите, пожалуйста, кнопку ниже:");
        message.setReplyMarkup(markup);

        try {
            // Отправляем сообщение
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}