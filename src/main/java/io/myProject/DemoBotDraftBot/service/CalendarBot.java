package io.myProject.DemoBotDraftBot.service;

import io.myProject.DemoBotDraftBot.config.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public CalendarBot(BotConfig config) {
        this.config = config;
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
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        // Создаем кастомную клавиатуру с двумя кнопками для текущего и следующего месяца
        InlineKeyboardMarkup keyboardMarkup = createCalendarKeyboard();

        // Создаем сообщение с клавиатурой
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите месяц:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            // Отправляем сообщение в чат
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createCalendarKeyboard() {
        // Получаем текущую дату
        LocalDate currentDate = LocalDate.now();

        // Создаем кнопки для текущего и следующего месяца
        InlineKeyboardButton currentMonthButton = new InlineKeyboardButton();
        currentMonthButton.setText(currentDate.getMonth().toString());
        currentMonthButton.setCallbackData(currentDate.format(DateTimeFormatter.ofPattern("MM-yyyy")));

        InlineKeyboardButton nextMonthButton = new InlineKeyboardButton();
        nextMonthButton.setText(currentDate.plusMonths(1).getMonth().toString());
        nextMonthButton.setCallbackData(currentDate.plusMonths(1).format(DateTimeFormatter.ofPattern("MM-yyyy")));

        // Создаем строку кнопок
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(currentMonthButton);
        row.add(nextMonthButton);

        // Создаем клавиатуру с одной строкой кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        // Создаем разметку клавиатуры
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    /*
    Да, вы можете вызвать `CalendarBot` в своем собственном Telegram-боте. Просто импортируйте класс `CalendarBot` и создайте экземпляр бота в своем коде. Затем вызовите метод `connect()` у экземпляра бота, чтобы он начал прослушивать обновления.

Вот пример, как это можно сделать в вашем коде:

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyBot extends TelegramLongPollingBot {

    private CalendarBot calendarBot = new CalendarBot();

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка обновлений вашего бота
    }

    @Override
    public String getBotUsername() {
        // Возвращает имя вашего бота
        return "YourBotUsername";
    }

    @Override
    public String getBotToken() {
        // Возвращает токен вашего бота
        return "YourBotToken";
    }

    public static void main(String[] args) {
        MyBot bot = new MyBot();

        // Создаем и запускаем экземпляр CalendarBot
        Thread calendarBotThread = new Thread(() -> {
            // Подключаем и запускаем CalendarBot
            bot.calendarBot.connect();
        });
        calendarBotThread.start();

        // Здесь можно добавить ваш собственный код и логику

        try {
            // Запускаем вашего бота
            bot.connect();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

В данном примере, мы создаем экземпляр `CalendarBot` внутри `MyBot` и запускаем его в отдельном потоке. Затем, в основном потоке, мы запускаем ваш бот вызовом метода `connect()`.

Обратите внимание, что вам необходимо заменить текст `"YourBotUsername"` и `"YourBotToken"` на соответствующие значения для вашего бота.
     */

}