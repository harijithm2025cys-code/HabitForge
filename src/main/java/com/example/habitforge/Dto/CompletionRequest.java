package com.example.habitforge.Dto;

import java.time.LocalDate;

public class CompletionRequest {

    private LocalDate completionDate;

    public CompletionRequest() {
    }

    public CompletionRequest(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }
}
