package com.example.habitforge.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.habitforge.Dto.CompletionRequest;
import com.example.habitforge.Dto.StreakResponse;
import com.example.habitforge.Service.CompletionService;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin
public class CompletionController {

    private final CompletionService completionService;

    public CompletionController(CompletionService completionService) {
        this.completionService = completionService;
    }

    @PostMapping("/{habitId}/complete")
    public ResponseEntity<StreakResponse> completeHabit(
            @PathVariable Long habitId,
            @RequestBody(required = false) CompletionRequest request) {
        StreakResponse response = completionService.completeHabit(habitId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
