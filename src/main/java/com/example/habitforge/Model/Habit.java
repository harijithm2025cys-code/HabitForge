package com.example.habitforge.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Habit name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Frequency type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrequencyType frequencyType;

    // Stored as comma-separated 1-7 numbers (e.g., "1,3,5" for Mon, Wed, Fri)
    private String targetDays;

    public Habit() {
    }

    public Habit(Long id, String name, String description, FrequencyType frequencyType, String targetDays) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frequencyType = frequencyType;
        this.targetDays = targetDays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
