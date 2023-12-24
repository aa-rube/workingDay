package app.bot.controller;

import app.bot.config.BotConfig;
import app.bot.enviroment.message.AdminMessage;
import app.bot.enviroment.message.EmployeeMessage;
import app.bot.enviroment.message.StartMessage;
import app.bot.service.EmployeeHandler;
import app.bot.service.ItemsHandler;
import app.factory.model.WorkingDay;
import app.factory.service.ExcelUpdater;
import app.factory.service.RedisService;
import app.factory.util.WorkingDayFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

@Controller
public class Chat extends TelegramLongPollingBot {
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private StartMessage startMessage;
    @Autowired
    private EmployeeMessage employeeMessage;
    @Autowired
    private EmployeeHandler employeeHandler;
    @Autowired
    private AdminMessage adminMessage;
    @Autowired
    private ItemsHandler itemsHandler;
    @Autowired
    private WorkingDayFormatter workingDayByText;
    @Autowired
    private RedisService redis;
    private final HashMap<Long, Integer> chatIdMsgId = new HashMap<>();
    private final HashMap<Long, WorkingDay> currentDayInfo = new HashMap<>();
    private final HashMap<Long, Integer> forChooseDate = new HashMap<>();
    private final HashSet<Long> addNewEmployee = new HashSet<>();
    private final HashSet<Long> addNewItem = new HashSet<>();
    private final HashMap<Long, Integer> addNewBatch = new HashMap<>();
    private final HashSet<Long> inputNewWorkingTime = new HashSet<>();

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
    
    public Long getAdminChatId() {
        return botConfig.getAdminId();
    }

    @Scheduled(fixedRate = 60000 / 3)
    private void updateExcel() {
        for (WorkingDay data : redis.getAllObjects()) {
            try {
                ExcelUpdater.writeData(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        redis.deleteAllObjects();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();

            clearWaitingLists(chatId);
            employeeCallBackData(chatId, data);
            adminsCallBackData(chatId, data);
            return;
        }

        if (update.hasMessage()) {
            System.out.println(update.getMessage().getChatId());
            textMessageHandle(update);
        }
    }

    private void employeeCallBackData(Long chatId, String data) {

        if (data.equals("/start")) {
            executeMsg(startMessage.getStart(chatId, chatId.equals(getAdminChatId())));
        }

        if (data.equals("reportStart")) {
            currentDayInfo.put(chatId, new WorkingDay());
            executeMsg(employeeMessage.startReport(chatId));
            return;
        }

        if (data.contains("sortedNames_")) {
            executeMsg(employeeMessage.getSortedList(chatId, data));
            return;
        }

        if (data.contains("person_")) {
            currentDayInfo.get(chatId).setFullName(employeeHandler.getFullNameById(data).toUpperCase());
            executeMsg(employeeMessage.extraDayOrMain(chatId, currentDayInfo.get(chatId).getFullName()));
        }

        if (data.contains("toDayIs_")) {
            currentDayInfo.get(chatId).setExtraDay(Integer.parseInt(data.split("_")[1]) == 1);
            forChooseDate.put(chatId, 0);
            executeMsg(employeeMessage.whatIsDay(chatId, 0));
            return;
        }

        if (data.contains("change_")) {
            forChooseDate.put(chatId, employeeHandler.getMinusOrPlusDay(data, forChooseDate.get(chatId)));
            editeMsgToDate(chatId, employeeMessage.whatIsDayObject(chatId, forChooseDate.get(chatId)));
            return;
        }

        if (data.contains("reportFrom_")) {
            inputNewWorkingTime.add(chatId);
            forChooseDate.remove(chatId);
            currentDayInfo.get(chatId).setLocalDateTime(employeeHandler.getDateTimeFromData(data));
            currentDayInfo.get(chatId).setWorkingTime(currentDayInfo.get(chatId).isExtraDay() ? 4.0 : 7.2);
            executeMsg(employeeMessage.getCheckAndContinue(chatId, currentDayInfo.get(chatId), 0));
            return;
        }

        if (data.equals("employeeContinue")) {
            executeMsg(employeeMessage.getItemsList(chatId));
            return;
        }

        if (data.contains("employeeItem_")) {
            currentDayInfo.get(chatId).setItem(employeeHandler.getItem(data));
            executeMsg(employeeMessage.getBatchList(chatId, currentDayInfo.get(chatId).getItem()));
            return;
        }

        if (data.contains("employeeBatch_")) {
            currentDayInfo.get(chatId).setBatch(employeeHandler.findItemsBatch(data));
            executeMsg(employeeMessage.getVolumeOptions(chatId));
            return;
        }

        if (data.contains("vol_")) {
            currentDayInfo.get(chatId).setLevel(employeeHandler.getVolume(data));
            executeMsg(employeeMessage.coefficientOptions(chatId));
        }

        if (data.contains("coef_")) {
            currentDayInfo.get(chatId).setCoefficient(employeeHandler.getVolume(data));
            executeMsg(employeeMessage.finishMessage(chatId, currentDayInfo.get(chatId), false));
            return;
        }

        if (data.equals("employeeEnd")) {
            System.out.println(data);
            deleteOldMessage(chatId);
            SendMessage msg = employeeMessage.finishMessage(chatId, currentDayInfo.get(chatId), true);
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            writeAndClear(chatId, msg);
        }
    }

    private void writeAndClear(Long chatId, SendMessage msg) {
        try {
            redis.saveObject(currentDayInfo.get(chatId));
            currentDayInfo.remove(chatId);

        } catch (Exception e) {
            try {
                execute(adminMessage.exceptionMsg(getAdminChatId(), msg));
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }

        executeMsg(startMessage.getStart(chatId, chatId.equals(getAdminChatId())));
    }

    private void adminsCallBackData(Long chatId, String data) {
        if (data.equals("3")) {
            deleteOldMessage(chatId);
            Thread thread = new Thread(() -> {
                updateExcel();
                try {
                    execute(adminMessage.getExcelFile(chatId));
                    executeMsg(startMessage.getStart(chatId, chatId.equals(getAdminChatId())));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }

        if (data.equals("0")) {
            executeMsg(adminMessage.whatAreWeGoingToDo(chatId, 1));
        }

        if (data.equals("1")) {
            executeMsg(adminMessage.whatAreWeGoingToDo(chatId, 0));
        }

        if (data.equals("backToStart")) {
            executeMsg(startMessage.getStart(chatId, chatId.equals(getAdminChatId())));
        }

        if (data.contains("delEmployee")) {
            executeMsg(adminMessage.getPeopleSortedList(chatId, null));
        }

        if (data.equals("addEmployee")) {
            addNewEmployee.add(chatId);
            executeMsg(adminMessage.addNewEmployee(chatId));
        }

        if (data.contains("sorted_")) {
            executeMsg(adminMessage.getPeopleSortedList(chatId, data));
        }

        if (data.contains("itemOptions")) {
            executeMsg(adminMessage.itemOptions(chatId));
        }

        if (data.contains("addItem")) {
            addNewItem.add(chatId);
            executeMsg(adminMessage.addNewItem(chatId));
        }

        if (data.contains("EditItem_")) {
            addNewItem.remove(chatId);
            addNewBatch.put(chatId, Integer.valueOf(data.split("_")[1]));
            executeMsg(adminMessage.addBatch(chatId, Integer.parseInt(data.split("_")[1])));
        }

        if (data.equals("delItem")) {
            deleteOldMessage(chatId);
            executeMsg(adminMessage.listItemsToDelete(chatId, ""));
        }
    }

    private void textMessageHandle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (text.equals("/start")) {
            executeMsg(startMessage.getStart(chatId, chatId.equals(getAdminChatId())));
            return;
        }

        if (text.contains("/delete_") && chatId.equals(getAdminChatId())) {
            try {
                execute(adminMessage.deleteEmployeeMsg(chatId, employeeHandler.deleteEmployeeById(text)));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (text.contains("ВАШ ОТЧЕТ ЗА") && text.contains("ФИО:")) {
            currentDayInfo.remove(chatId);
            currentDayInfo.put(chatId, workingDayByText.getWorkingDayFromText(text));
            SendMessage msg = employeeMessage.finishMessage(chatId, currentDayInfo.get(chatId), true);

            try {
                execute(msg);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            writeAndClear(chatId, msg);
        }

        if (inputNewWorkingTime.contains(chatId)) {
            currentDayInfo.get(chatId)
                    .setWorkingTime(employeeHandler
                            .getNewWorkingTimeValue(text, currentDayInfo.get(chatId).isExtraDay()));
            executeMsg(employeeMessage.getCheckAndContinue(chatId, currentDayInfo.get(chatId), 1));
            return;
        }

        if (addNewEmployee.contains(chatId) && chatId.equals(getAdminChatId())) {
            executeMsg(adminMessage.executeNewEmployee(chatId, employeeHandler.addNewEmployee(text)));
            return;
        }

        if (addNewItem.contains(chatId)) {
            executeMsg(adminMessage.executeNewItem(chatId, itemsHandler.addNewItem(text), text));
            return;
        }

        if (addNewBatch.containsKey(chatId)) {

            if (text.contains("/deleteBatch_")) {
                executeMsg(adminMessage.deleteBatch(chatId, addNewBatch.get(chatId), text));
                return;
            }

            executeMsg(adminMessage
                    .executeNewBatch(chatId, itemsHandler
                            .addNewBatch(text, addNewBatch.get(chatId)), text, addNewBatch.get(chatId)));
            return;
        }

        if (text.contains("/removeItem_")) {
            executeMsg(adminMessage.listItemsToDelete(chatId, text));
        }
    }

    private void editeMsgToDate(Long chatId, Object[] obj) {
        EditMessageText edit = new EditMessageText();
        edit.setMessageId(chatIdMsgId.get(chatId));
        edit.setChatId(chatId);
        edit.setText(obj[0].toString().toUpperCase());
        edit.setReplyMarkup((InlineKeyboardMarkup) obj[1]);
        edit.setParseMode(ParseMode.HTML);
        edit.enableHtml(true);
        try {
            execute(edit);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearWaitingLists(Long chatId) {
        addNewBatch.remove(chatId);
        addNewItem.remove(chatId);
        addNewEmployee.remove(chatId);
    }

    private void executeMsg(SendMessage msg) {
        try {
            deleteOldMessage(Long.valueOf(msg.getChatId()));

            if (msg.getReplyMarkup() != null) {
                chatIdMsgId.put(Long.valueOf(msg.getChatId()), execute(msg).getMessageId());
                return;
            }

            execute(msg);
        } catch (Exception e) {

            try {
                if (msg.getReplyMarkup() != null) {
                    chatIdMsgId.put(Long.valueOf(msg.getChatId()), execute(msg).getMessageId());
                    return;
                }
                execute(msg);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    private void deleteOldMessage(Long chatId) {
        DeleteMessage del = new DeleteMessage();
        del.setChatId(chatId);
        del.setMessageId(chatIdMsgId.get(chatId));

        try {
            execute(del);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}