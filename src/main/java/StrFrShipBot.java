import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class StrFrShipBot extends TelegramLongPollingBot {
    private List<Long> longList = new LinkedList<>();
    private List<Pair> listPair = new LinkedList<>();


    private class Pair {
        private Long chatIDa;
        private Long chatIDb;

        private Pair(Long chatIDa, Long chatIDb) {
            this.chatIDa = chatIDa;
            this.chatIDb = chatIDb;
        }

    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Long chatID = update.getMessage().getChatId();
            Message message = update.getMessage();

            if (update.getMessage().getText().equals("/start")) {
                SendMessage sendMessage = new SendMessage()
                        .setChatId(chatID)
                        .setText("Наш бот поможет вам найти собеседника.\n" +
                                "Что же, давайте начнем.::smile::\n" +
                                "Запустите команду /find и как только появится собеседник,\n" +
                                "Мы вас с ним свяжем. А пока ждите");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (update.getMessage().getText().equals("/find")) {

                SendMessage mess = new SendMessage()
                        .setChatId(chatID)
                        .setText("Сейчас мы тебе найдем кого нибудь");

                try {
                    execute(mess);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                if (longList.size() == 0) {
                    longList.add(chatID);
                    System.out.println("added: " + chatID);
                } else {
                    Long addedChatId = longList.get(0);
                    if (!chatID.equals(addedChatId)) {
                        listPair.add(this.connectTwoChatId(chatID, addedChatId));
                        SendMessage mess_textA = new SendMessage()
                                .setText("Собеседник найден. Начинайте общаться.)")
                                .setChatId(addedChatId);
                        try {
                            execute(mess_textA);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        SendMessage mess_textB = new SendMessage()
                                .setText("Собеседник найден. Начинайте общаться.)")
                                .setChatId(chatID);
                        try {
                            execute(mess_textB);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        longList.remove(addedChatId);
                        System.out.println("added: " + chatID + " <_> " + addedChatId);

                    }


                }
            } else if (update.getMessage().getText().equals("/break")) {
                Pair pair = this.getCoincidences(chatID);
                this.disconectChatID(pair);
                SendMessage sendMessage = new SendMessage()
                        .setText("Вы порвали с собеседником. Чтобы заново начать поиск\n" +
                                "Напишите /find")
                        .setChatId(chatID);

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                assert pair != null;
                if (!pair.chatIDa.equals(chatID)) {
                    SendMessage mess_text = new SendMessage()
                            .setText("Собеседник покинул беседу. \nВы идете нахер")
                            .setChatId(pair.chatIDa);

                    try {
                        execute(mess_text);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    SendMessage mess_text = new SendMessage()
                            .setText("Собеседник покинул беседу. \nВы идете нахер")
                            .setChatId(pair.chatIDb);

                    try {
                        execute(mess_text);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }


            } else if (listPair.size() != 0) {
                Pair pair = this.getCoincidences(chatID);

                assert pair != null;
                if (pair.chatIDa.equals(chatID)) {
                    SendMessage mess = new SendMessage()
                            .setChatId(pair.chatIDb)
                            .setText(message.getText());

                    try {
                        execute(mess);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    SendMessage mess = new SendMessage()
                            .setChatId(pair.chatIDa)
                            .setText(message.getText());
                    try {
                        execute(mess);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private void disconectChatID(Pair pair) {
        this.listPair.remove(pair);
    }


    // find any coincidences to connect
    private Pair getCoincidences(Long chatID) {
        for (Pair pair :
                listPair) {
            if (pair.chatIDa.equals(chatID) || pair.chatIDb.equals(chatID)) {
                return pair;
            }
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return BotConfig.USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.TOKEN;
    }


    // connect two chatid
    private Pair connectTwoChatId(Long chatIDa, Long chatIDb) {
        return new Pair(chatIDa, chatIDb);
    }


}
