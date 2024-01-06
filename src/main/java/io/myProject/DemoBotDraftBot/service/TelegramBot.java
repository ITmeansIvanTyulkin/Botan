package io.myProject.DemoBotDraftBot.service;

import io.myProject.DemoBotDraftBot.DemoBotDraftBotApplication;
import io.myProject.DemoBotDraftBot.config.BotConfig;
import io.myProject.DemoBotDraftBot.waitings.Waitings;
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
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
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

    static final String ANSWER_TO_USER = "Ответ пользователю: ";
    final BotConfig config;
    private static final Map<Long, List<String>> userState = new HashMap<>();
    boolean isSearchCommandPressed = false;
    private static final Logger LOGGER = Logger.getLogger(TelegramBot.class.getName());

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

                case "Москва":
                    sendMessage(chatId, "Вы выбрали город Москва.");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("город Москва");
                    sendParameterOfTheNewMenuMoscow(chatId);
                    break;

                case "Питер":
                    sendMessage(chatId, "Вы выбрали город Санкт-Петербург.");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("город Санкт-Петербург");
                    sendParameterOfTheNewMenuPiter(chatId);
                    break;

                case "Сочи":
                    sendMessage(chatId, "Вы выбрали город Сочи.");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("город Сочи");
                    sendParameterOfTheNewMenuSochi(chatId);
                    break;

                case "В рамках МКАДа":
                    sendMessage(chatId, "Вы выбрали объект недвижимости в рамках МКАДа!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("В рамках МКАДа");
                    sendParameterMenu(chatId);
                    break;

                case "За МКАДом":
                    sendMessage(chatId, "Вы выбрали объект недвижимости за МКАДом!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("За МКАДом");
                    sendParameterMenu(chatId);
                    break;

                case "В рамках ЗСД":
                    sendMessage(chatId, "Вы выбрали объект недвижимости в рамках ЗСД!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("В рамках ЗСД");
                    sendParameterMenu(chatId);
                    break;

                case "За ЗСД":
                    sendMessage(chatId, "Вы выбрали объект недвижимости За ЗСД!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("За ЗСД");
                    sendParameterMenu(chatId);
                    break;

                case "Ленобласть":
                    sendMessage(chatId, "Вы выбрали объект недвижимости в Ленобласти!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Ленобласть");
                    sendParameterMenu(chatId);
                    break;

                case "Адлер":
                    sendMessage(chatId, "Вы выбрали объект недвижимости в Адлере!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("район Адлер");
                    sendParameterMenu(chatId);
                    break;

                case "Лазоревское":
                    sendMessage(chatId, "Вы выбрали объект недвижимости в Лазоревском!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("район Лазоревское");
                    sendParameterMenu(chatId);
                    break;

                case "город Сочи":
                    sendMessage(chatId, "Вы выбрали объект недвижимости в Сочи!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("город Сочи");
                    sendParameterMenu(chatId);
                    break;

                case "\u2699 Настроить поиск":
                    sendMessage(chatId, "Отлично! Давайте настроим поиск по Вашим параметрам!");
                    sendTheButtonToCustomize(chatId);
                    break;

//                    try {
//                        sendAMessageToSeeVariants(chatId);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    break;

                case "\u2936 Начать поиск заново!":
                    sendMessage(chatId, "Окей, не страшно, начинаем заново :)");
                    searchCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "Комната":
                    sendMessage(chatId, "Вы выбрали аренду комнаты в квартире! Помимо Вас в квартире будут проживать ещё и другие арендаторы. Однако, это самый выгодный по цене вариант. Обычно именно с аренды комнаты в квартире начинают свою жизнь приезжие в большом городе. Позже Вы сможете переехать из комнаты в студию или квартиру, или даже дом!");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Комната");
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "Студия":
                    sendMessage(chatId, "Вы выбрали студию. " + DemoBotDraftBotApplication.botTalking());
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Студия");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendParameterPeople(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "1-комнатная":
                    sendMessage(chatId, "Вы выбрали 1-комнатную квартиру! " + DemoBotDraftBotApplication.botTalking());
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("1-комнатная квартира");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendParameterPeople(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "2-комнатная":
                    sendMessage(chatId, "Вы выбрали 2-комнатную квартиру! " + DemoBotDraftBotApplication.botTalking());
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("2-комнатная квартира");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendParameterPeople(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "3-комнатная":
                    sendMessage(chatId, "Вы выбрали 3-комнатную квартиру! " + DemoBotDraftBotApplication.botTalking());
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("3-комнатная квартира");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendParameterPeople(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "Дом":
                    sendMessage(chatId, "Вы выбрали дом! " + DemoBotDraftBotApplication.botTalking());
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Дом");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendParameterPeople(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "1 человек":
                    sendMessage(chatId, "Записал Ваш ответ: Вы будете проживать один(-а), хорошо. ");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Проживать будет 1 человек.");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIAmReadyToRepeatYourChoice(chatId);
                    break;

                case "2 человека":
                    sendMessage(chatId, "Записал Ваш ответ: проживать будут 2 человека, хорошо. ");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Проживать будет 2 человека.");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIAmReadyToRepeatYourChoice(chatId);
                    break;

                case "3 человека":
                    sendMessage(chatId, "Записал Ваш ответ: проживать будут 3 человека, хорошо. ");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Проживать будет 3 человека.");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIAmReadyToRepeatYourChoice(chatId);
                    break;

                case "4 человека":
                    sendMessage(chatId, "Записал Ваш ответ: проживать будут 4 человека, хорошо. ");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Проживать будет 4 человека.");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIAmReadyToRepeatYourChoice(chatId);
                    break;

                case "5 человек":
                    sendMessage(chatId, "Записал Ваш ответ: проживать будут 5 человек, хорошо. ");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Проживать будет 5 человек.");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIAmReadyToRepeatYourChoice(chatId);
                    break;

                case "Более 5 человек":
                    sendMessage(chatId, "Записал Ваш ответ: проживать будут более 5 человек, хорошо. ");
                    userState.computeIfAbsent(chatId, k -> new ArrayList<>()).add("Проживать будут более 5 человек.");
                    try {
                        Thread.sleep(WAITFOR3SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIDoYourReplies(chatId);
                    try {
                        Thread.sleep(WAITFOR5SEC);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendAMessageThatIAmReadyToRepeatYourChoice(chatId);
                    break;

                case "Посмотреть варианты":
                    sendMessage(chatId, "Отлично, смотрим!");
                    sendPaymentImage(chatId);
                    break;

                case "Начать поиск заново!":
                    sendMessage(chatId, "Отлично, ищем заново!");
                    searchCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                default:
                    sendMessageWithButtonChooseTheCity(chatId, "Я не знаю, что на это ответить... Напишите подробно Ваш вопрос, а я поищу свободного оператора, который сможет Вам помочь!");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer1 = "Привет, " + name + ", приятно познакомиться! Меня зовут Бот@Н, я могу найти для Вас жилую недвижимость для аренды," +
                " как посуточно, так и длительно. Я работаю в городах Москва, Санкт-Петербург и Сочи. Чтобы" +
                " найти комнату, студию, квартиру, апартаменты, таунхаус или даже дом для проживания, нажмите, пожалуйста, 'search' и выберете" +
                " город, в котором Вы желаете арендовать. Далее заполните всего 3 поля и через несколько секунд я выдам Вам результат! Я работаю" +
                " только с собственниками помещений, НЕ риэлторами. Желаю Вам, как можно скорее найти прекрасный вариант для аренды!";
        LOGGER.info(ANSWER_TO_USER + name);
        sendMessageWithButtonChooseTheCity(chatId, answer1);
    }

    private void searchCommandReceived(long chatId, String name) {
        String answer3 = "Итак, " + name + ", пора найти лучшее предложение по аренде для Вас! Внизу есть три города - Москва, Санкт-Петербург и Сочи - в" +
                " которых я работаю. Выберете тот город, в котором Вы желаете найти аренду жилой недвижимости - на сутки или длительно!";
        LOGGER.info(ANSWER_TO_USER + name);
        sendMessageWithButtonChooseTheCity(chatId, answer3);
    }

    private void helpCommandReceived(long chatId, String name) {
        String answer4 = name + ", вот информация о том, кто мы и что конкретно я умею делать. Итак, я имею доступ к базе данных недвижимости собственников" +
                " жилья. В нашей базе данных более 2500 различных предложений. Все объекты недвижимости проверены, но иногда попадаются" +
                " риэлторы, которые притворяются собственниками и желают на этом заработать. Если Вы, вдруг, столкнётесь с подобным  фактом," +
                " пожалуйста, не попадайтесь. Ваше право, конечно, работать с риэлторами, но разве Вас не смущает момент, что человек уже обманывает Вас," +
                " притворяясь владельцем квартиры, студии или комнаты? Так что работать ли с таким человеком - сугубо на Ваше усмотрение. Я же, в свою" +
                " очередь, напоминаю, что ищу для Вас интересующие Вас помещения ТОЛЬКО от собственника. Что ещё я умею: 1. Чтобы найти жилую недвижимость в аренду," +
                " нажмите /search. 2. Кнопку /help Вы уже нажали, раз читаете этот текст. Итак, я готов к работе!";
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
        Thread.sleep(WAITFOR5SEC);
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
        message.setText("Отлично, я записал Ваши пожелания! Я буду искать для Вас недвижимость по параметрам: \n\n" + parameters + "\n\nТеперь давайте настроим поиск по цене аренды, выбору района, этажу и желаемой площади квартиры. Для этого нажмите кнопку  <b> 'Настроить поиск' </b>. Если нужен поиск по другим параметрам, нажмите кнопку <b> 'Нет! Начать заново!' </b>");
        message.setParseMode(ParseMode.HTML);

        ReplyKeyboardMarkup replyMarkup = confirmTheChoice();
        message.setReplyMarkup(replyMarkup);
        userState.get(chatId).clear();

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
        replyMarkup.setOneTimeKeyboard(false);

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
        message.setText("Для настройки поиска, нажмите, пожалуйста, кнопку ниже:");
        message.setReplyMarkup(markup);

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