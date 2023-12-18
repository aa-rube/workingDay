package app.bot.enviroment.message;

import app.bot.enviroment.keyboard.AdminKeyboard;
import app.factory.model.Batch;
import app.factory.service.ItemService;
import app.factory.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;

@Service
public class AdminMessage {
    @Autowired
    private AdminKeyboard adminKeyboard;
    @Autowired
    private PeopleService peopleService;
    @Autowired
    private ItemService itemService;
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


    public SendMessage getPeopleSortedList(Long chatId, String data) {
        builder.setLength(0);

        if (data == null) {
            builder.append("Выберите диапазон:\n");
            return getSendMessage(chatId, builder.toString(), adminKeyboard.getSortedLastNames());
        }

        String[] letters = data.split("_");
        builder.append("Тут можно удалить сотруднка из базы данных бота:\n");
        peopleService.filterPeopleBySecondNameRange(letters[1].charAt(0), letters[2].charAt(0))
                .stream()
                .map(p -> builder.append(p.getSecondName()).append(" ").append(p.getName())
                        .append(" ").append(p.getThirdName()).append("\n")
                        .append("/delete_").append(p.getId()).append("\n"))
                .forEachOrdered(sb -> {
                });
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getSortedLastNames());
    }

    public SendMessage whatAreWeGoingToDo(Long chatId, int i) {
        builder.setLength(0);
        if (i == 0) {
            return getSendMessage(chatId, "Редактируем список сотрудников", adminKeyboard.employeesOptions());

        } else {
            return getSendMessage(chatId, "Редактируем список изделий", adminKeyboard.itemsOptions());
        }
    }

    public SendMessage deleteEmployeeMsg(Long chatId, boolean b) {
        if (b) {
            return getSendMessage(chatId, "Сотрудник успешно удален", null);
        }
        return getSendMessage(chatId, "Что-то пошло не так, повторите", null);
    }

    public SendMessage addNewEmployee(Long chatId) {
        return getSendMessage(chatId, "Введите ФИО работника." +
                "\nВАЖНО! Сначала фамилия, потом имя, потом очество(если есть)" +
                "\nИмена можно вносить списком через пернос на новую строку", adminKeyboard.getBack());
    }

    public SendMessage executeNewEmployee(Long chatId, boolean b) {
        if (b) {
            return getSendMessage(chatId, "Данные сохранены", adminKeyboard.getBack());
        }

        return getSendMessage(chatId, "Попробуйте еще раз", adminKeyboard.getBack());
    }

    public SendMessage itemOptions(Long chatId) {
        return getSendMessage(chatId, "Выберите изделие или добавьте новое",
                adminKeyboard.itemsList(itemService.getAllItems()));
    }

    public SendMessage executeNewItem(Long chatId, boolean b, String text) {
        builder.setLength(0);

        if (b) {
            builder.append("Изделие \"").append(text.trim()).append("\" успешно добавлено. Можно добавить еще одно.\n\n")
                    .append("Партии настраиваются позже, в параметрах каждого изделия");
            return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack());
        }

        builder.append("Что-то пошло не так, повторите попытку");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack());
    }

    public SendMessage addNewItem(Long chatId) {
        return getSendMessage(chatId, "Введите наименование изделия:", adminKeyboard.getBack());
    }

    private String getBatchListForItem(int i) {
        StringBuilder string = new StringBuilder();

        for (Batch b : itemService.getItemById(i).getBatches()) {
            string.append("Партия: ").append(b.getBatchNumber()).append("\n")
                    .append("/deleteBatch_").append(b.getId()).append("\n\n");
        }
        return string.toString();
    }

    public SendMessage addBatch(Long chatId, int i) {
        builder.setLength(0);
        builder.append("Введите наименование НОВОЙ партии для изделия: <b>")
                .append(itemService.getItemById(i).getName())
                .append("</b> или нажмите синюю ссылку, что бы партию удалить:\n\n")
                .append(getBatchListForItem(i));
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack());

    }

    public SendMessage executeNewBatch(Long chatId, boolean b, String text, int i) {
        if (b) {
            builder.append("Партия ").append(text).append( " сохранена!\n")
                    .append("Вы можете ввести значение новой партии или удалить старую запись.\n\n")
                    .append(getBatchListForItem(i));
            return getSendMessage(chatId,builder.toString(), adminKeyboard.getBack());
        }

        return getSendMessage(chatId, "Что-то пошло не так", adminKeyboard.getBack());

    }

    public SendMessage exceptionMsg(Long adminChatId, SendMessage msg) {
        builder.setLength(0);

        builder.append(msg.getText()).append("\n\n")
                .append("Произошла ошибка записи в фаил. Данные от работника экстренно отправлены в рабочий чат для логирования");

        return getSendMessage(adminChatId, builder.toString(), null);
    }
}