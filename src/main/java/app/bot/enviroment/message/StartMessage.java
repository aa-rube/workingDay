package app.bot.enviroment.message;

import app.bot.enviroment.keyboard.StartKeyboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class StartMessage {
    @Autowired
    private StartKeyboard startKeyboard;
    private final StringBuilder builder = new StringBuilder();

    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getStart(Long chatId, boolean isAdmin) {
        builder.setLength(0);
        builder.append("<b>Вас приветствует</b> ПОМОГАТОР\n\n");

        if (isAdmin) {
            builder.append("Это бот для взаимодействия с операторами. \nПо информации из бота они смогут быстро и легко ")
                    .append("заполнить отчет о пределанной работе за смену, а бот аккуратно запишет все в excel файл.");
            return getSendMessage(chatId, builder.toString(), startKeyboard.adminStart());
        }

        builder.append("Он поможет вам заполнить информацию о проделаной работе за смену.\n")
                .append("Нажмите заполнить отчет, чтобы начать");
        return getSendMessage(chatId, builder.toString().toUpperCase(), startKeyboard.reportStart());
    }

}
