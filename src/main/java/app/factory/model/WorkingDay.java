package app.factory.model;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkingDay {
    private int id;
    private Long chatId;
    private String fullName;
    private boolean isExtraDay;
    private LocalDateTime localDateTime;
    private Item item;
    private String batch;
    private double workingTime;
    private String level;
    private String coefficient;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public double getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(double workingTime) {
        this.workingTime = workingTime;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExtraDay() {
        return isExtraDay;
    }

    public void setExtraDay(boolean extraDay) {
        isExtraDay = extraDay;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
