package io.myProject.DemoBotDraftBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class DemoBotDraftBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoBotDraftBotApplication.class, args);
	}

	static final String[] phrasesCongratulation = {
			"Отличный выбор! Без сомнений - это самый правильный вариант недвижимости в аренду! ",                                  // 1.
			"Превосходный выбор! Такую недвижимость выбирают самые умные! ",                                                        // 2.
			"Достойный выбор! Это популярный вариант у моих клиентов! ",                                                            // 3.
			"Вы сделали прекрасный выбор! Недвижимость с такими параметрами выбирают очень умные люди! ",                           // 4.
			"Хорошее решение! Очень правильный выбор! ",                                                                            // 5.
			"Итак, отлично! Должен сказать, что на сегодняшний день это самый популярный вариант! ",                                // 6.
			"Поздравляю с прекрасным выбором! Очень многие люди нашли себе недвижимость в аренду точно с такими же параметрами! ",  // 7.
			"Чудесное решение! Это популярный выбор у многих людей! ",                                                              // 8.
			"Итак, прекрасно! Такую недвижимость выбирают многие! ",                                                                // 9.
			"Чёткое решение и прекрасный выбор! У меня многие клиенты выбирают именно такую недвижимость! "                         // 10.
	};

	// Создаю генератор фраз бота из массива String[] phrases.
	public static String botTalking() {
		Random randomPhrases = new Random();
		int index = randomPhrases.nextInt(phrasesCongratulation.length);
		return phrasesCongratulation[index];
	}
}