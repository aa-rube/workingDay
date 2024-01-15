package app.bot.enviroment.message;

import app.bot.enviroment.keyboard.AdminKeyboard;
import app.bot.service.AdminHandler;
import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.model.WorkingDay;
import app.factory.service.BatchService;
import app.factory.service.ItemService;
import app.factory.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class AdminMessage {
    @Autowired
    private AdminKeyboard adminKeyboard;
    @Autowired
    private PeopleService peopleService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BatchService batchService;
    @Autowired
    private AdminHandler adminHandler;
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
                "\nИмена можно вносить списком через пернос на новую строку", adminKeyboard.getBack(1));
    }

    public SendMessage executeNewEmployee(Long chatId, boolean b) {
        if (b) {
            return getSendMessage(chatId, "Данные сохранены", adminKeyboard.getBack(1));
        }

        return getSendMessage(chatId, "Попробуйте еще раз", adminKeyboard.getBack(1));
    }

    public SendMessage itemOptions(Long chatId) {
        return getSendMessage(chatId, "Выберите изделие или добавьте новое",
                adminKeyboard.itemsList(itemService.getAllItems()));
    }

    public SendMessage executeNewItem(Long chatId, boolean b, String text) {
        builder.setLength(0);
        String adding = text.split("\n").length > 1 ? " " : " \"" + text.trim() + "\" ";
        if (b) {
            builder.append("Изделие").append(adding).append("успешно добавлено. Можно добавить еще.\n\n")
                    .append("Партии настраиваются позже, в параметрах каждого изделия");
            return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack(2));
        }

        builder.append("Что-то пошло не так, повторите попытку");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack(1));
    }

    public SendMessage addNewItem(Long chatId) {
        return getSendMessage(chatId, "Введите наименование изделия:", adminKeyboard.getBack(2));
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
        builder.append("Введите наименование НОВОЙ партии для изделия:  <b>")
                .append(itemService.getItemById(i).getName())
                .append("\n</b> или нажмите синюю ссылку, что бы партию удалить:\n\n")
                .append(getBatchListForItem(i));
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack(2));

    }

    public SendMessage executeNewBatch(Long chatId, boolean b, String text, int i) {
        builder.setLength(0);
        if (b) {
            builder.append("Партия ").append(text).append(" сохранена!\n")
                    .append("Вы можете ввести значение новой партии или удалить старую запись.\n\n")
                    .append(getBatchListForItem(i));
            return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack(2));
        }

        return getSendMessage(chatId, "Что-то пошло не так", adminKeyboard.getBack(2));
    }

    public SendMessage deleteBatch(Long chatId, int itemId, String text) {
        batchService.deleteBatch(text);
        builder.setLength(0);
        builder.append("Партия удалена!\n\n")
                .append("Введите наименование НОВОЙ партии для изделия:  <b>")
                .append(itemService.getItemById(itemId).getName())
                .append("\n</b> или нажмите синюю ссылку, что бы партию удалить:\n\n").append(getBatchListForItem(itemId));
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack(2));
    }

    public SendMessage exceptionMsg(Long adminChatId, SendMessage msg) {
        builder.setLength(0);

        builder.append(msg.getText()).append("\n\n")
                .append("Произошла ошибка записи в фаил. Данные от работника экстренно отправлены в этот чат для логирования");

        return getSendMessage(adminChatId, builder.toString(), null);
    }

    public SendMessage listItemsToDelete(Long chatId, String text) {
        builder.setLength(0);

        if (!text.isEmpty()) {
            itemService.deleteItem(text);
            builder.append("Изделие удалено!\n\n");
        }
        builder.append("Список изделий для удаления.\nПри удалении изделия парти так-же удаляются\n\n");
        int x = 1;
        for (Item i : itemService.getAllItems()) {
            builder.append(x).append(". ").append(i.getName()).append("\n")
                    .append("/removeItem_").append(i.getId()).append("\n\n");
            x++;
        }
        builder.append("Для удаления нажмите на ссылку под записью");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.deleteItemAndOptions());
    }

    public SendDocument getExcelFile(Long chatId) {
        LocalDate date = LocalDate.now();

        builder.setLength(0);
        builder.append("Отчет за ").append(date.getMonthValue()).append(".").append(date.getYear());
        SendDocument document = new SendDocument();
        document.setDocument(new InputFile(adminHandler.renameAndCopyFile(date)));
        document.setChatId(chatId);
        document.setCaption(builder.toString());
        return document;
    }


    public SendMessage addNewFile(Long chatId) {
        builder.setLength(0);

        builder.append("Загрузите новый файл для записи отчета сотрудников.");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBack(4));
    }

    public SendMessage getChangeFileStatus(Long chatId, boolean success) {
        builder.setLength(0);
        if (success) {
            return getSendMessage(chatId, "Файл успешно заменен.", null);
        }
        return getSendMessage(chatId, "Что-то пошло не так", null);
    }

    public SendMessage wasReported(Long adminChatId, Set<String> allStrings) {
        builder.setLength(0);
        LocalDate date = LocalDate.now().minusDays(1);
        builder.append("<b>Отчет за ").append(date.getDayOfMonth()).append(".")
                .append(date.getMonthValue()).append(" сдали:</b>\n");

        List<String> employsWithOutReport = adminHandler.getEmploysWithOutReport(allStrings);
        if (employsWithOutReport.isEmpty()) {
            builder.append("Ни один сотрудник не сдал отчет");
            return getSendMessage(adminChatId, builder.toString(), null);
        }

        for (String s : allStrings) {
            builder.append(s).append("\n");
        }

        builder.append("\n\n_________________________________\nСотрудники, кто не сдавал отчет:\n\n");

        for (String string : employsWithOutReport) {
            builder.append(string).append("\n");
        }
        return getSendMessage(adminChatId, builder.toString(), null);
    }

    public SendMessage reportExist(Long chatId, WorkingDay workingDay) {
        builder.setLength(0);

        builder.append("Сотрудник ").append(workingDay.getFullName()).append(" пытается внести изменения в отчет за ")
                .append(workingDay.getLocalDateTime().getDayOfMonth()).append(".")
                .append(workingDay.getLocalDateTime().getMonthValue()).append(".")
                .append(workingDay.getLocalDateTime().getYear()).append(", ")
                .append(workingDay.isExtraDay() ? "переработка": "основной график");
        return getSendMessage(chatId, builder.toString(), null);
    }
}