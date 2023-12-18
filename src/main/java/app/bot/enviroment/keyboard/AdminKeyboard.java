package app.bot.enviroment.keyboard;

import app.factory.model.Item;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminKeyboard {
    public InlineKeyboardMarkup getSortedLastNames() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton ad = new InlineKeyboardButton();
        ad.setText("А - Д");
        ad.setCallbackData("sorted_А_Д");
        firstRow.add(ad);

        InlineKeyboardButton ek = new InlineKeyboardButton();
        ek.setText("Е - K");
        ek.setCallbackData("sorted_Е_К");
        firstRow.add(ek);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton lo = new InlineKeyboardButton();
        lo.setText("Л - О");
        lo.setCallbackData("sorted_Л_О");
        secondRow.add(lo);

        InlineKeyboardButton ps = new InlineKeyboardButton();
        ps.setText("П - С");
        ps.setCallbackData("sorted_П_С");
        secondRow.add(ps);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton ty = new InlineKeyboardButton();
        ty.setText("Т - Я");
        ty.setCallbackData("sorted_Т_Я");
        thirdRow.add(ty);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("ЗАКРЫТЬ");
        back.setCallbackData("backToStart");
        backRow.add(back);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup employeesOptions() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton del = new InlineKeyboardButton();
        del.setText("Удалить сотрудника".toUpperCase());
        del.setCallbackData("delEmployee");
        firstRow.add(del);

        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Добавить сотрудника".toUpperCase());
        add.setCallbackData("addEmployee");
        firstRow.add(add);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("ЗАКРЫТЬ");
        back.setCallbackData("backToStart");
        backRow.add(back);


        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }


    public InlineKeyboardMarkup getBack() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("ЗАКРЫТЬ");
        back.setCallbackData("backToStart");
        backRow.add(back);

        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup itemsOptions() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton del = new InlineKeyboardButton();
        del.setText("Удалить изделие".toUpperCase());
        del.setCallbackData("delItem");
        firstRow.add(del);

        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Параметры изделий".toUpperCase());
        add.setCallbackData("itemOptions");
        firstRow.add(add);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("ЗАКРЫТЬ");
        back.setCallbackData("backToStart");
        backRow.add(back);


        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }


    public InlineKeyboardMarkup itemsList(List<Item> items) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        for (Item item : items) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            InlineKeyboardButton one = new InlineKeyboardButton();
            one.setText(item.getName());
            one.setCallbackData("EditItem_" + item.getId());
            line.add(one);
            keyboardMatrix.add(line);
        }

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Добавить".toUpperCase());
        add.setCallbackData("addItem");

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("ЗАКРЫТЬ");
        back.setCallbackData("backToStart");
        backRow.add(add);
        backRow.add(back);

        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
