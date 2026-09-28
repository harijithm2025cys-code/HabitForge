package com.example.habitforge.Dto;

import com.example.habitforge.Model.FrequencyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateHabitRequest {

    @NotBlank(message = "Habit name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Frequency type cannot be null")
    private FrequencyType frequencyType;

    // Optional for DAILY, required for SPECIFIC_DAYS (e.g., "1,3,5")
    private String targetDays;

    public CreateHabitRequest() {
    }

    public CreateHabitRequest(String name, String description, FrequencyType frequencyType, String targetDays) {
        this.name = name;
        this.description = description;
        this.frequencyType = frequencyType;
        this.targetDays = targetDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public String getTargetDays() {
        return targetDays;
    }

    public void setTargetDays(String targetDays) {
        this.targetDays = targetDays;
    }
}
