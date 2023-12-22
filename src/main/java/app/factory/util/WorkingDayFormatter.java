package app.factory.util;

import app.factory.model.Item;
import app.factory.model.WorkingDay;
import app.factory.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkingDayFormatter {
    @Autowired
    private ItemService itemService;
    public WorkingDay getWorkingDayFromText(String text) {
        String[] data = text.split("\n");

        WorkingDay workingDay = new WorkingDay();

        Pattern datePattern = Pattern.compile("ВАШ ОТЧЕТ ЗА (\\d{2}\\.\\d{2})");
        Pattern fioPattern = Pattern.compile("ФИО: (.+)");
        Pattern hoursPattern = Pattern.compile("ЧАСЫ: (.+)");
        Pattern productPattern = Pattern.compile("ИЗДЕЛИЕ: (.+)");
        Pattern rankPattern = Pattern.compile("РАЗРЯД: (.+)");
        Pattern coefficientPattern = Pattern.compile("КОЭФИЦИЕНТ: (.+)");

        Matcher dateMatcher = datePattern.matcher(data[0]);
        Matcher fioMatcher = fioPattern.matcher(data[2]);
        Matcher hoursMatcher = hoursPattern.matcher(data[3]);
        Matcher productMatcher = productPattern.matcher(data[4]);
        Matcher rankMatcher = rankPattern.matcher(data[5]);
        Matcher coefficientMatcher = coefficientPattern.matcher(data[6]);

        if (dateMatcher.find()) {
            workingDay.setLocalDateTime(convertStringToLocalDateTime(dateMatcher.group(1)));
        }

        if (fioMatcher.find()) {
            workingDay.setFullName(fioMatcher.group(1).trim());
        }

        if (hoursMatcher.find()) {
            workingDay.setExtraDay(hoursMatcher.group(1).toLowerCase().contains("основное время"));
            workingDay.setWorkingTime(Double.parseDouble(hoursMatcher.group(1).trim().split(",")[0]));

            if (workingDay.getWorkingTime() > 7.2) {
                workingDay.setWorkingTime(7.2);
            }
        }

        if (productMatcher.find()) {
            Item item = itemService.findByName(productMatcher.group(1).trim().split(",")[0]);
            workingDay.setItem(item.getName());
            item.getBatches().stream()
                    .filter(b -> b.getBatchNumber().equals(productMatcher.group(1).trim().split(",")[1].replaceAll(" ", "")))
                    .findFirst().ifPresent(b -> workingDay.setBatch(b.getBatchNumber()));
        }

        if (rankMatcher.find()) {
            workingDay.setLevel(rankMatcher.group(1).trim());
        }

        if (coefficientMatcher.find()) {
            workingDay.setCoefficient(coefficientMatcher.group(1).trim());
        }

        return workingDay;
    }

    private LocalDateTime convertStringToLocalDateTime(String dateString) {
        int currentYear = LocalDateTime.now().getYear();
        String dateTimeString = (dateString + "." + currentYear).trim().replaceAll(" ", "").concat("T00:00:00");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
