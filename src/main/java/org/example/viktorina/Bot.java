/*package org.example.viktorina;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "TestYourLuckTestBot";
    }

    @Override
    public String getBotToken() {
        return "7775787427:AAEAXxi20t3KoIaeVpVvRAIP5C3szRD1vis";
    }


    HashMap<Long, Quiz> quizzes = new HashMap<>();
    // Карта для хранения состояний викторин

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
            if (update.hasMessage()) {
                Message message = update.getMessage();
                String text = message.getText();
                long userId = message.getFrom().getId();

                if ("/start".equals(message.getText())) {
                    sendText(userId, "Привет. Это тест моего первого бота!");
                    startQuiz(userId, message.getChatId());
                } else {
                    handleAnswer(update); // Обрабатываем ответ пользователя
                }
            }
        }

        private void startQuiz(long userId, long chatId) {
            Quiz quiz = new Quiz(this, userId, chatId);
                quizzes.put(userId, quiz);
                // Сохраняем состояние викторины для данного пользователя
            quiz.start();
        }
    private void handleAnswer(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        long userId = message.getFrom().getId();

        //Quiz quiz = new Quiz(this, userId, message.getChatId());
        //quiz.handleAnswer(text);
        Quiz quiz = quizzes.get(userId); // Получаем сохраненное состояние викторины для данного пользователя
        if (quiz != null) {
            quiz.handleAnswer(text);
        } else {
            sendText(userId, "Сначала начните викторину командой /start.");
        }
    }
    }

    class Quiz {
        private final Bot bot;
        private final long userId;
        private final long chatId;
        private int currentQuestionIndex = 0;
        private final List<Question> questions;

        public Quiz(Bot bot, long userId, long chatId) {
            this.bot = bot;
            this.userId = userId;
            this.chatId = chatId;
            this.questions = initializeQuestions();
        }

        private List<Question> initializeQuestions() {
            List<Question> questions = new ArrayList<>();

            questions.add(new Question(
                    "Сколько в языке программирования Java есть примитивов?",
                    "8",
                    "Правильный ответ: 8."
            ));

            questions.add(new Question(
                    "Сколько в реляционных (SQL) базах данных существует типов связей между таблицами?",
                    "3",
                    "Правильный ответ: 3 (один к одному, один ко многим, многие ко многим)."
            ));

            questions.add(new Question(
                    "С помощью какой команды в системе контроля версий Git можно просмотреть авторов различных строк в одном файле?",
                    "git blame",
                    "Правильная команда: git blame."
            ));

            questions.add(new Question(
                    "Какие методы HTTP-запросов вы знаете?", //GET, POST, PUT, DELETE
                    "get post put delete",
                    "Правильные методы: GET, POST, PUT, DELETE."
            ));

            return questions;
        }

        public void start() {
            if (currentQuestionIndex >= questions.size()) {
                endQuiz();
                return;
            }

            Question question = questions.get(currentQuestionIndex);
            bot.sendText(chatId, String.format("Вопрос %d. %s", currentQuestionIndex + 1, question.text));
        }

        public void handleAnswer(String answer) {
            Question question = questions.get(currentQuestionIndex);

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