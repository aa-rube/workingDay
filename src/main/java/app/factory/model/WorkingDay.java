package app.factory.model;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkingDay {
    private int id;
    private String fullName;
    private boolean isExtraDay;
    private LocalDateTime localDateTime;
    private String item;
    private String batch;
    private double workingTime;
    private String level;
    private String coefficient;
    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
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

    @Override
    public String toString() {
        return super.toString();
    }
}
