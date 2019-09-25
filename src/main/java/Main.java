import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private final Logger logger = LogManager.getLogManager().getLogger(Main.class.getName());


    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new StrFrShipBot());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
