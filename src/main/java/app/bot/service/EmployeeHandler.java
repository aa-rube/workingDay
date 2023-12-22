package app.bot.service;

import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.model.Person;
import app.factory.model.WorkingDay;
import app.factory.service.BatchService;
import app.factory.service.ItemService;
import app.factory.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmployeeHandler {
    @Autowired
    private PeopleService peopleService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BatchService batchService;

    public String getFullNameById(String text) {
        int id = Integer.parseInt(text.split("_")[1]);
        Person p = peopleService.findPersonById(id);
        return p.getSecondName() + " " + p.getName() + " " + p.getThirdName();
    }


    public Integer getMinusOrPlusDay(String data, Integer integer) {
        int dataInt = Integer.parseInt(data.split("_")[1]);
        if (dataInt == 0) {
            return integer - 1;
        }
        return integer + 1;
    }

    public int compareDateToCurrentMonth(LocalDateTime dateTime) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        if (dateTime.isBefore(startOfMonth)) {
            return -1;
        } else if (dateTime.isAfter(endOfMonth)) {
            return 1;
        } else {
            return 0;
        }
    }

    public LocalDateTime getDateTimeFromData(String data) {
        return LocalDateTime.parse(data.split("_")[1]);
    }

    public boolean deleteEmployeeById(String text) {
        int id = Integer.parseInt(text.split("_")[1]);
        return peopleService.deleteById(id);
    }

    public boolean addNewEmployee(String text) {
        String[] names = text.split("\n");
        TreeSet<String> setNames = new TreeSet<>();
        Collections.addAll(setNames, names);

        if (setNames.size() > 1) {
            for (String name : setNames) {
                splitOnGup(name);
            }
            return true;
        }

       return splitOnGup(text);
    }

    private boolean splitOnGup(String name) {
        String[] parts = name.split(" ");
        if (parts.length > 1) {
            Person person = new Person();
            person.setSecondName(parts[0]);
            person.setName(parts[1]);

            if (parts.length > 2) {
                person.setThirdName(parts[2]);
            }
            peopleService.save(person);
            return true;
        }
        return false;
    }

    public String getItem(String data) {
        return itemService.getItemById(Integer.parseInt(data.split("_")[1])).getName();
    }

    public double getNewWorkingTimeValue(String text, boolean isExtraDay) {
        return parseDouble(text.replaceAll(",", "."), isExtraDay);
    }

    private double parseDouble(String input, boolean isExtraDay) {
        String cleanedInput = input.replaceAll(" ", ".").replaceAll("[^\\d.]", "");

        String truncatedInput = cleanedInput.substring(0, Math.min(cleanedInput.length(), 3));

        if (truncatedInput.isEmpty() || !truncatedInput.matches("\\d+\\.?\\d*")) {
            if (isExtraDay) {
                return 4;
            }
            return 7.2;
        }
        double parsedValue = Double.parseDouble(truncatedInput);
        return Math.min(parsedValue, 7.2);
    }

    public String findItemsBatch(String data) {
        int id = Integer.parseInt(data.split("_")[1]);
        return batchService.getBatchById(id).getBatchNumber();
    }

    public String getVolume(String data) {
        return data.split("_")[1];
    }

}

