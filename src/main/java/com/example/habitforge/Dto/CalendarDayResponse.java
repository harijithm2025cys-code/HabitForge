package com.example.habitforge.Dto;

import java.time.LocalDate;

public class CalendarDayResponse {

    private LocalDate date;
    private boolean required;
    private boolean completed;

    public CalendarDayResponse() {
    }

    public CalendarDayResponse(LocalDate date, boolean required, boolean completed) {
        this.date = date;
        this.required = required;
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
