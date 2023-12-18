package app.bot.enviroment.keyboard;

import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.model.Person;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
public class EmployeeKeyboard {
    public InlineKeyboardMarkup getSortedLastNames(List<Person> people) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        if (people != null) {
            for (Person p : people) {
                List<InlineKeyboardButton> loop = new ArrayList<>();
                InlineKeyboardButton main = new InlineKeyboardButton();
                main.setText(p.getSecondName() + " " + p.getName() + " " + p.getThirdName());
                main.setCallbackData("person_" + p.getId());
                loop.add(main);
                keyboardMatrix.add(loop);
            }
        }

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton ad = new InlineKeyboardButton();
        ad.setText("А - Д");
        ad.setCallbackData("sortedNames_А_Д");
        firstRow.add(ad);

        InlineKeyboardButton ek = new InlineKeyboardButton();
        ek.setText("Е - K");
        ek.setCallbackData("sortedNames_Е_К");
        firstRow.add(ek);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton lo = new InlineKeyboardButton();
        lo.setText("Л - О");
        lo.setCallbackData("sortedNames_Л_О");
        secondRow.add(lo);

        InlineKeyboardButton ps = new InlineKeyboardButton();
        ps.setText("П - С");
        ps.setCallbackData("sortedNames_П_С");
        secondRow.add(ps);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton ty = new InlineKeyboardButton();
        ty.setText("Т - Я");
        ty.setCallbackData("sortedNames_Т_Я");
        thirdRow.add(ty);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup mainOrExtraDay() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton main = new InlineKeyboardButton();
        main.setText("Основной".toUpperCase());
        main.setCallbackData("toDayIs_0");
        firstRow.add(main);

        InlineKeyboardButton extra = new InlineKeyboardButton();
        extra.setText("Переработка".toUpperCase());
        extra.setCallbackData("toDayIs_1");
        firstRow.add(extra);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup dayBackOrDayNext(String formattedDay, String fullDate) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton main = new InlineKeyboardButton();
        main.setText("День назад".toUpperCase());
        main.setCallbackData("change_0");
        firstRow.add(main);

        InlineKeyboardButton extra = new InlineKeyboardButton();
        extra.setText("день вперед".toUpperCase());
        extra.setCallbackData("change_1");
        firstRow.add(extra);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton ok = new InlineKeyboardButton();
        ok.setText(("Выбрать " + formattedDay + " число").toUpperCase());
        ok.setCallbackData("reportFrom_" + fullDate);
        secondRow.add(ok);

        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(firstRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup dayBack() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton main = new InlineKeyboardButton();
        main.setText("День назад".toUpperCase());
        main.setCallbackData("change_0");
        firstRow.add(main);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup dayNext() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton extra = new InlineKeyboardButton();
        extra.setText("день вперед".toUpperCase());
        extra.setCallbackData("change_1");
        firstRow.add(extra);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getItemsList(List<Item> items) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        for (Item item : items) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            InlineKeyboardButton one = new InlineKeyboardButton();
            one.setText(item.getName());
            one.setCallbackData("employeeItem_" + item.getId());
            line.add(one);
            keyboardMatrix.add(line);
        }

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBatchList(List<Batch> batches) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        for (Batch batch : batches) {
            List<InlineKeyboardButton> line = new ArrayList<>();
            InlineKeyboardButton one = new InlineKeyboardButton();
            one.setText(batch.getBatchNumber());
            one.setCallbackData("employeeBatch_" + batch.getId());
            line.add(one);
            keyboardMatrix.add(line);
        }

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup approveAndContinue(boolean isEnd) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton con = new InlineKeyboardButton();
        con.setText("продолжить".toUpperCase());
        con.setCallbackData(isEnd ? "employeeEnd" : "employeeContinue");
        firstRow.add(con);


        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton son = new InlineKeyboardButton();
        son.setText("нет, начать сначала".toUpperCase());
        son.setCallbackData("/start");
        secondRow.add(son);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup volume() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        String volume = "vol_";
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton one = new InlineKeyboardButton();
        one.setText("2/4".toUpperCase());
        one.setCallbackData(volume + "2/4");
        firstRow.add(one);

        InlineKeyboardButton two = new InlineKeyboardButton();
        two.setText("2/8".toUpperCase());
        two.setCallbackData(volume + "2/8");
        firstRow.add(two);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton three = new InlineKeyboardButton();
        three.setText("3/16".toUpperCase());
        three.setCallbackData(volume + "3/16");
        secondRow.add(three);

        InlineKeyboardButton four = new InlineKeyboardButton();
        four.setText("3/24".toUpperCase());
        four.setCallbackData(volume + "3/24");
        secondRow.add(four);

        InlineKeyboardButton five = new InlineKeyboardButton();
        five.setText("3/4".toUpperCase());
        five.setCallbackData(volume + "3/4");
        secondRow.add(five);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton six = new InlineKeyboardButton();
        six.setText("4/16".toUpperCase());
        six.setCallbackData(volume + "4/16");
        thirdRow.add(six);

        InlineKeyboardButton seven = new InlineKeyboardButton();
        seven.setText("4/8".toUpperCase());
        seven.setCallbackData(volume + "4/8");
        thirdRow.add(seven);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup coefficient() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        String add = "coef_";

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton con = new InlineKeyboardButton();
        con.setText("1".toUpperCase());
        con.setCallbackData(add + "1");
        firstRow.add(con);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton son = new InlineKeyboardButton();
        son.setText("1,15".toUpperCase());
        son.setCallbackData(add + "1,15");
        secondRow.add(son);

        InlineKeyboardButton qon = new InlineKeyboardButton();
        qon.setText("1,2".toUpperCase());
        qon.setCallbackData(add + "1,2");
        secondRow.add(qon);

        InlineKeyboardButton won = new InlineKeyboardButton();
        won.setText("1,3".toUpperCase());
        won.setCallbackData(add + "1,3");
        secondRow.add(won);

        InlineKeyboardButton ron = new InlineKeyboardButton();
        ron.setText("1,4".toUpperCase());
        ron.setCallbackData(add + "1,4");
        secondRow.add(ron);

        InlineKeyboardButton uon = new InlineKeyboardButton();
        uon.setText("1,5".toUpperCase());
        uon.setCallbackData(add + "1,5");
        secondRow.add(uon);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
