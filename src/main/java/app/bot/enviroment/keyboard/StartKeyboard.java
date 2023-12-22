package app.bot.enviroment.keyboard;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartKeyboard {

    public InlineKeyboardMarkup reportStart() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton main = new InlineKeyboardButton();
        main.setText("Заполнить отчет".toUpperCase());
        main.setCallbackData("reportStart");
        firstRow.add(main);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }


    public InlineKeyboardMarkup adminStart() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Изделия");
        add.setCallbackData("0");
        firstRow.add(add);

        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Сотрудники");
        list.setCallbackData("1");
        firstRow.add(list);

        List<InlineKeyboardButton> second = new ArrayList<>();
        InlineKeyboardButton excel = new InlineKeyboardButton();
        excel.setText("Получить отчет");
        excel.setCallbackData("excel");
        second.add(excel);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(second);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
