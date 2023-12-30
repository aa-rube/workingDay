package app.bot.enviroment.message;

import app.bot.service.EmployeeHandler;
import app.bot.enviroment.keyboard.EmployeeKeyboard;
import app.factory.model.Item;
import app.factory.model.Person;
import app.factory.model.WorkingDay;
import app.factory.service.ItemService;
import app.factory.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
public class EmployeeMessage {
    @Autowired
    private EmployeeHandler employeeHandler;
    @Autowired
    private EmployeeKeyboard employeeKeyboard;
    @Autowired
    private PeopleService peopleService;
    @Autowired
    private ItemService itemService;
    private final StringBuilder builder = new StringBuilder();

    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text.toUpperCase());
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }


    public SendMessage startReport(Long chatId) {
        builder.setLength(0);

        builder.append("Найдите ваши ФИО в списке. Имена отсортированы по фамилии: ");
        return getSendMessage(chatId, builder.toString(), employeeKeyboard.getSortedLastNames(null));
    }

    public SendMessage getSortedList(Long chatId, String data) {
        builder.setLength(0);
        String[] letters = data.split("_");
        builder.append("Список сотрудников. Нажмите на кнопку Вашим вашим именем:\n\n");
        List<Person> peopleList = peopleService.filterPeopleBySecondNameRange(letters[1].charAt(0), letters[2].charAt(0))
                .stream().toList();

        if (peopleList.isEmpty()) {
            builder.setLength(0);
            builder.append("Похоже, для выбранных букв нет доступных имен. Попробуйте выбрать другой вариант");
            return getSendMessage(chatId, builder.toString(), employeeKeyboard.getSortedLastNames(null));
        }
        return getSendMessage(chatId, builder.toString(), employeeKeyboard.getSortedLastNames(peopleList));
    }


    public SendMessage extraDayOrMain(Long chatId, String fullName) {
        builder.setLength(0);
        builder.append("<b>").append(fullName).append("</b>\n\n")
                .append("Вы сегодня по <b>основному</b> графику или в <b>переработку</b>?");

        return getSendMessage(chatId, builder.toString(), employeeKeyboard.mainOrExtraDay());
    }

    public SendMessage whatIsDay(Long chatId, int i) {
        builder.setLength(0);
        LocalDateTime dateTime = LocalDateTime.now().plusDays(i);
        int result = employeeHandler.compareDateToCurrentMonth(dateTime);

        switch (result) {
            case -1:
                builder.append("Выбранная дата раньше текущего месяца.");
                return getSendMessage(chatId, builder.toString(), employeeKeyboard.dayNext());
            case 1:
                builder.append("Выбранная дата находится позже текущего месяца.");
                return getSendMessage(chatId, builder.toString(), employeeKeyboard.dayBack());
            default:
                String formattedDay = DateTimeFormatter.ofPattern("dd").format(dateTime);
                builder.append("На какую дату заполняем отчет:\n")
                        .append("<b>Число: ").append(formattedDay).append("</b>");
                return getSendMessage(chatId, builder.toString(),
                        employeeKeyboard.dayBackOrDayNext(formattedDay, dateTime.toString()));
        }
    }

    public Object[] whatIsDayObject(Long chatId, int i) {
        builder.setLength(0);
        LocalDateTime dateTime = LocalDateTime.now().plusDays(i);
        int result = employeeHandler.compareDateToCurrentMonth(dateTime);

        switch (result) {
            case -1:
                builder.append("Выбранная дата раньше текущего месяца.");
                return new Object[]{builder.toString(), employeeKeyboard.dayNext()};

            case 1:
                builder.append("Выбранная дата находится позже текущего месяца.");
                return new Object[]{builder.toString(), employeeKeyboard.dayBack()};
            default:
                String formattedDay = DateTimeFormatter.ofPattern("dd").format(dateTime);
                builder.append("На какую дату заполняем отчет:\n")
                        .append("<b>Число: ").append(formattedDay).append("</b>");
                return new Object[]{builder.toString(), employeeKeyboard.dayBackOrDayNext(formattedDay, dateTime.toString())};
        }
    }

    public SendMessage getCheckAndContinue(Long chatId, WorkingDay workingDay, int i) {
        builder.setLength(0);

        builder.append("РАБОТНИК: <b>").append(workingDay.getFullName()).append("</b>\n\n")
                .append("ДАТА: <b>").append(getDateLine(workingDay.getLocalDateTime())).append("</b>\n\n")
                .append(workingDay.isExtraDay() ? "<b>ПЕРЕРАБОТКА</b>\n\n" : "")
                .append("РАБОЧЕЕ ВРЕМЯ: <b>").append(workingDay.getWorkingTime()).append(" ЧАСА</b>");
        if (i == 1) {
            builder.append("<b>\nПРОВЕРЬТЕ НОВОE ЗНАЧЕНИЕ ОТРАБОТАННОГО ВРЕМЕНИ!\n\n</b>");
        }
        if (i == 0){
            builder.append("\n\n");
        }
                builder.append("Если время не верно - введите в строке сообщения свое значение рабочего времени, далее нажмите стрелку.\n\n")
                .append("НАЖМИТЕ <b>ПРОДОЛЖИТЬ</b>, ЕСЛИ ВСЕ ВЕРНО");

        return getSendMessage(chatId, builder.toString(), employeeKeyboard.approveAndContinue(false));
    }

    public SendMessage getItemsList(Long chatId) {
        builder.setLength(0);
        builder.append("Выберите наименование изделия: ");

        return getSendMessage(chatId, builder.toString(), employeeKeyboard.getItemsList(itemService.getAllItems()));
    }

    private String getDateLine(LocalDateTime dateTime) {
        long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.from(dateTime));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM (EEEE)", new Locale("ru"));
        return switch ((int) daysDifference) {
            case 0 -> "сегодня, " + dateTime.format(formatter);
            case 1 -> "завтра, " + dateTime.format(formatter);
            case -1 -> "вчера, " + dateTime.format(formatter);
            case -2 -> "позавчера, " + dateTime.format(formatter);
            case 2 -> "после завтра, " + dateTime.format(formatter);
            default -> dateTime.format(formatter);
        };
    }

    public SendMessage getBatchList(Long chatId, String item) {
        Item itemObject = itemService.findByName(item);

        builder.setLength(0);
        builder.append("Изделие <b>").append(item).append("</b>\n\n")
                .append("<b>Выберите партию изделия:</b>");
        return getSendMessage(chatId, builder.toString(), employeeKeyboard.getBatchList(itemObject.getBatches()));
    }

    public SendMessage getVolumeOptions(Long chatId) {
        builder.setLength(0);
        builder.append("Выберите свой разряд (выполняемая операция приравнивается к определенному разряду работ)");
        return getSendMessage(chatId, builder.toString(), employeeKeyboard.volume());
    }

    public SendMessage coefficientOptions(Long chatId) {
        builder.setLength(0);
        builder.append("Выберите ваш коэффициент");
        return getSendMessage(chatId, builder.toString(), employeeKeyboard.coefficient());
    }

    public SendMessage finishMessage(Long chatId, WorkingDay day, boolean isEnd) {
        builder.setLength(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofPattern("d.M.yyyy", new Locale("ru", "RU"));
        String data = "<b>" + day.getLocalDateTime().format(dateFormatter) + "</b>";

        builder.append(isEnd ?"Информация записана!\n\n<code>Ваш отчет за " + data + "\n\n"
                        : "<b>проверьте всю информацию:</b>\n\n" + "Отчет за: " + data).append(isEnd ? "\n" : "❗️\n\n")

                .append("ФИО: <b>").append(day.getFullName()).append(isEnd ? "" : "❗️\n").append("</b>\n")
                .append("Часы:  <b>").append(day.getWorkingTime()).append(", ")
                .append(day.isExtraDay() ? "переработка</b>\n" : "основное время</b>").append(isEnd ? "\n" : "❗️\n\n")
                .append("Изделие: <b>").append(day.getItem()).append(", ").append(day.getBatch()).append(isEnd ? "" : "❗️\n").append("</b>\n")
                .append("Разряд: <b>").append(day.getLevel()).append(isEnd ? "" : "❗️\n").append("</b>\n")
                .append("коэффициент: <b>").append(day.getCoefficient()).append(isEnd ? "" : "❗️").append("</b>\n\n")
                .append(isEnd ? "</code>" : "Нажмите <b>\"продолжить\"</b>, если все верно");

        if (isEnd) {
            return getSendMessage(chatId, builder.toString(),null);
        }

        return getSendMessage(chatId, builder.toString(), employeeKeyboard.approveAndContinue(true));
    }
}
