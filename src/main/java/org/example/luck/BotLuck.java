package org.example.luck;

import java.util.HashMap;
import java.util.Random;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


public class BotLuck extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "TestYourLuckTestBot";
    }

    @Override
    public String getBotToken() {
        return "7775787427:AAEAXxi20t3KoIaeVpVvRAIP5C3szRD1vis";
    }

    public void sendText(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private final Random random = new Random();
    private final Map<Long, GameState> games = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String text = message.getText();
            long userId = message.getFrom().getId();
            String fullName = message.getFrom().getFirstName() + " " + message.getFrom().getLastName();
            long chatId = message.getChatId();

            onMessageReceived(text, userId, fullName, String.valueOf(chatId));
        }
    }

    public void onMessageReceived(String text, long userId, String fullName, String chatId) {
        if ("/start".equals(text)) {
            sendText(userId, "Привет, " + fullName + "! Давай сыграем в игру 'Проверь свою удачу'."
                    + " Выбери максимальное число (n), до которого я буду загадывать."
                    + " Максимальное число должно быть больше нуля.");
        } else if (games.containsKey(userId)) {
            handleGuess(text, userId);
        } else {
            try {
                int maxNumber = Integer.parseInt(text.trim());
                if (maxNumber > 0) {
                    startGame(userId, Long.parseLong(chatId), maxNumber);
                } else {
                    sendText(userId, "Максимальное число должно быть больше нуля. Попробуй еще раз.");
                }
            } catch (NumberFormatException e) {
                sendText(userId, "Пожалуйста, введи корректное число.");
            }
        }
    }

    private void startGame(long userId, long chatId, int maxNumber) {
        int secretNumber = random.nextInt(maxNumber) + 1;
        GameState gameState = new GameState(secretNumber, maxNumber);
        games.put(userId, gameState);
        sendText(chatId, "Я загадал число от 1 до " + maxNumber + ". Угадывайте!");
        double probability = 100d / maxNumber;
        sendText(userId, "Шанс угадать мое число: " + probability + "%");
    }

    private void handleGuess(String text, long userId) {
        GameState gameState = games.get(userId);
        if (gameState != null) {
            try {
                int guess = Integer.parseInt(text.trim());
                checkGuess(gameState, guess, userId);
            } catch (NumberFormatException e) {
                sendText(userId, "Пожалуйста, введите целое число.");
            }
        } else {
            sendText(userId, "Игра не найдена. Начни заново командой /start.");
        }
    }

    private void checkGuess(GameState gameState, int guess, long userId) {
        int maxNumber = gameState.maxNumber;

        if (guess == gameState.secretNumber) {
            sendText(userId, "Молодец! Ты угадал число: " + gameState.secretNumber + ". Продолжаем играть.");

            int newSecretNumber = random.nextInt(maxNumber) + 1;
            gameState.secretNumber = newSecretNumber;
            sendText(userId, "Новое число от 1 до " + maxNumber + ". Угадай!");
        } else if (guess < gameState.secretNumber) {
            gameState.incrementAttempts();
            int newSecretNumber = random.nextInt(maxNumber) + 1;
            gameState.secretNumber = newSecretNumber;
            sendText(userId, "Твое число меньше загаданного. Попробуй еще раз.");
        } else {
            gameState.incrementAttempts();
            int newSecretNumber = random.nextInt(maxNumber) + 1;
            gameState.secretNumber = newSecretNumber;
            sendText(userId, "Твое число больше загаданного. Попробуй еще раз.");
        }
    }

    private static class GameState {
        int secretNumber;
        int maxNumber;
        int attempts;

        public GameState(int secretNumber, int maxNumber) {
            this.secretNumber = secretNumber;
            this.maxNumber = maxNumber;
            this.attempts = 0;
        }

        public void incrementAttempts() {
            this.attempts++;
        }
    }
}



    /*  public void sendText(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            long userId = message.getFrom().getId();
            String chatId = message.getChatId().toString();

            String firstName = message.getFrom().getFirstName();
            String lastName = message.getFrom().getLastName();
            String fullName = firstName + (lastName == null ? "" : " " + lastName);

            if ("/start".equals(text)) {
                sendText(userId, "Привет, " + fullName + "! Давай сыграем в игру 'Проверь свою удачу'." +
                        " Выбери максимальное число (n), до которого я буду загадывать." +
                        " Максимальное число должно быть больше нуля.");
            } else if (games.containsKey(userId)) {
                handleGuess(update);
            } else {
                try {
                    int maxNumber = Integer.parseInt(text.trim());
                    if (maxNumber > 0) {
                        startGame(userId, Long.parseLong(chatId), maxNumber);
                    } else {
                        sendText(userId, "Максимальное число должно быть больше нуля. Попробуй еще раз.");
                    }
                } catch (NumberFormatException e) {
                    sendText(userId, "Пожалуйста, введи корректное число.");
                }
            }
        }
    }

    private void startGame(long userId, long chatId, int maxNumber) {
        int secretNumber = random.nextInt(maxNumber) + 1;
        GameState gameState = new GameState(secretNumber, maxNumber);
        games.put(userId, gameState);
        sendText(chatId, "Я загадал число от 1 до " + maxNumber + ". Угадывайте!");
            double probability = 100d / (maxNumber * maxNumber);
            sendText(userId, "Шанс угадать мое число: " + (probability) + "%. Удачи!");
    }

    private void handleGuess(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        long userId = message.getFrom().getId();

        GameState gameState = games.get(userId);
        if (gameState != null) {
            try {
                int guess = Integer.parseInt(text.trim());
                checkGuess(gameState, guess, userId);
            } catch (NumberFormatException e) {
                sendText(userId, "Пожалуйста, введите целое число.");
            }
        } else {
            sendText(userId, "Игра не найдена. Начни заново командой /start.");
        }
    }

    private void checkGuess(GameState gameState, int guess, long userId) {
        int maxNumber = 0;
        if (guess == gameState.secretNumber) {
            sendText(userId, "Поздравляю! Ты угадал правильное число: " + gameState.secretNumber + ". Игра продолжается.");
            maxNumber = gameState.maxNumber;

            int newSecretNumber = random.nextInt(maxNumber) + 1;
            gameState.secretNumber = newSecretNumber;

            sendText(userId, "Загадка продолжается. Новое число от 1 до " + maxNumber + ". Угадай!");
        } else if (guess < gameState.secretNumber) {
            sendText(userId, "Твое число меньше загаданного. Попробуй еще раз.");
            int secretNumber = random.nextInt(maxNumber) + 1;
            gameState.secretNumber = newSecretNumber;

        } else {
            sendText(userId, "Твое число больше загаданного. Попробуй еще раз.");
            int secretNumber = random.nextInt(maxNumber) + 1;
            gameState.secretNumber = newSecretNumber;

        }
    }

    private int calculateProbability(int maxNumber) {
        return maxNumber;
    }
}

class GameState {
    int secretNumber;
    int maxNumber;

    public GameState(int secretNumber, int maxNumber) {
        this.secretNumber = secretNumber;
        this.maxNumber = maxNumber;
    }
}
*/



    /*   HashMap<Long, org.example.luck.Quiz> quizzes = new HashMap<>();

    public void sendText(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            long userId = message.getFrom().getId();
            String chatId = message.getChatId().toString();

            String firstName = message.getFrom().getFirstName();
            String lastName = message.getFrom().getLastName();
            String fullName = firstName + (lastName == null ? "" : " " + lastName);

            if ("/start".equals(text)) {
                sendText(userId, "Привет, " + fullName + "! Рад тебя видеть.");
                startQuiz(userId, Long.parseLong(chatId));
            } else {
                handleAnswer(update);
            }
        }
    }




    private void startQuiz(long userId, long chatId) {
        org.example.luck.Quiz quiz = new org.example.luck.Quiz(this, userId, chatId);
        quizzes.put(userId, quiz);
        // Сохраняем состояние викторины для данного пользователя
        quiz.start();
    }
    private void handleAnswer(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        long userId = message.getFrom().getId();

        org.example.luck.Quiz quiz = quizzes.get(userId); // Получаем сохраненное состояние викторины для данного пользователя
        if (quiz != null) {
            quiz.handleAnswer(text);
        } else {
            sendText(userId, "Сначала начните викторину командой /start.");
        }
    }
}

class Quiz {
    private final org.example.luck.BotLuck bot;
    private final long userId;
    private final long chatId;
    private int currentQuestionIndex = 0;
    private final List<org.example.luck.Question> questions;

    public Quiz(org.example.luck.BotLuck bot, long userId, long chatId) {
        this.bot = bot;
        this.userId = userId;
        this.chatId = chatId;
        this.questions = initializeQuestions();
    }

    private List<org.example.luck.Question> initializeQuestions() {
        List<org.example.luck.Question> questions = new ArrayList<>();

        questions.add(new org.example.luck.Question(
                "Сколько в языке программирования Java есть примитивов?",
                "8",
                "Правильный ответ: 8."
        ));


        return questions;
    }

    public void start() {
        if (currentQuestionIndex >= questions.size()) {
            endQuiz();
            return;
        }

        org.example.luck.Question question = questions.get(currentQuestionIndex);
        bot.sendText(chatId, String.format("Вопрос %d. %s", currentQuestionIndex + 1, question.text));
    }

    public void handleAnswer(String answer) {
        org.example.luck.Question question = questions.get(currentQuestionIndex);

        if (question.isCorrect(answer)) {
            bot.sendText(chatId, "Верно!");
        } else {
            bot.sendText(chatId, question.feedback);
        }

        currentQuestionIndex++;
        start();
    }

    private void endQuiz() {
        bot.sendText(chatId, "Викторина завершена. Спасибо за участие!");
        bot.quizzes.remove(userId); // Удаляем викторину из карты, так как она завершена
    }
}

class Question {
    String text;
    String correctAnswer;
    String feedback;

    Question(String text, String correctAnswer, String feedback) {
        this.text = text;
        this.correctAnswer = correctAnswer.toLowerCase();
        this.feedback = feedback;
    }

    boolean isCorrect(String userAnswer) {
        return userAnswer.equals(correctAnswer);
    }
}
*/
