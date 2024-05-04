package io.myProject.BotanBot.bottalking;

import io.myProject.BotanBot.texts.TextsToRespond;

import java.util.Random;

public class BotTalking implements BotTalkingInterface {

    // Создаю генератор фраз бота из массива String[] phrases.
    @Override
    public String botTalking() {
        Random randomPhrases = new Random();
        int index = randomPhrases.nextInt(TextsToRespond.phrasesCongratulation.length);
        return TextsToRespond.phrasesCongratulation[index];
    }
}