package com.example.habitforge;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.habitforge.Controller.CompletionController;
import com.example.habitforge.Controller.HabitController;
import com.example.habitforge.Controller.StreakController;
import com.example.habitforge.Dto.CalendarDayResponse;
import com.example.habitforge.Dto.CompletionRequest;
import com.example.habitforge.Dto.CreateHabitRequest;
import com.example.habitforge.Dto.StreakResponse;
import com.example.habitforge.Exception.CompletionAlreadyExistsException;
import com.example.habitforge.Exception.HabitNotFoundException;
import com.example.habitforge.Exception.InvalidCompletionException;
import com.example.habitforge.Model.FrequencyType;
import com.example.habitforge.Model.Habit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HabitApiTests {

    @Autowired
    private HabitController habitController;

    @Autowired
    private CompletionController completionController;

    @Autowired
    private StreakController streakController;

    private static Long createdHabitId;

    @Test
    @Order(1)
    void test1_createHabit() {
        CreateHabitRequest request = new CreateHabitRequest(
                "Morning Jogging",
                "Run 3km every morning",
                FrequencyType.DAILY,
                null
        );

        ResponseEntity<Habit> response = habitController.createHabit(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Morning Jogging", response.getBody().getName());
        assertEquals(FrequencyType.DAILY, response.getBody().getFrequencyType());

        createdHabitId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void test2_getAllHabits() {
        ResponseEntity<List<Habit>> response = habitController.getAllHabits();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 1);
    }

    @Test
    @Order(3)
    void test3_getOneHabit() {
        ResponseEntity<?> response = habitController.getHabitById(createdHabitId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Habit);
        Habit habit = (Habit) response.getBody();
        assertEquals(createdHabitId, habit.getId());
        assertEquals("Morning Jogging", habit.getName());
    }

    @Test
    @Order(4)
    void test4_updateHabit() {
        CreateHabitRequest updateRequest = new CreateHabitRequest(
                "Morning Jogging & Stretching",
                "Run 3km and stretch 10 minutes",
                FrequencyType.DAILY,
                null
        );

        ResponseEntity<Habit> response = habitController.updateHabit(createdHabitId, updateRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Morning Jogging & Stretching", response.getBody().getName());
    }

    @Test
    @Order(5)
    void test5_completeHabit() {
        CompletionRequest request = new CompletionRequest(LocalDate.now());
        ResponseEntity<StreakResponse> response = completionController.completeHabit(createdHabitId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdHabitId, response.getBody().getHabitId());
        assertTrue(response.getBody().getCurrentStreak() >= 1);
        assertTrue(response.getBody().getBestStreak() >= 1);
    }

    @Test
    @Order(6)
    void test6_getStreak() {
        ResponseEntity<StreakResponse> response = streakController.getStreak(createdHabitId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdHabitId, response.getBody().getHabitId());
        assertTrue(response.getBody().getCurrentStreak() >= 1);
        assertTrue(response.getBody().getBestStreak() >= 1);
    }

    @Test
    @Order(7)
    void test7_getMonthlyCalendar() {
        LocalDate now = LocalDate.now();
        ResponseEntity<List<CalendarDayResponse>> response = streakController.getCalendar(
                createdHabitId,
                now.getYear(),
                now.getMonthValue()
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(now.lengthOfMonth(), response.getBody().size());

        boolean hasCompletedDay = false;
        for (CalendarDayResponse day : response.getBody()) {
            if (day.isCompleted()) {
                hasCompletedDay = true;
                break;
            }
        }
        assertTrue(hasCompletedDay, "Monthly calendar should have today marked as completed");
    }

    @Test
    @Order(8)
    void test8_duplicateCompletionShouldThrow409() {
        CompletionRequest request = new CompletionRequest(LocalDate.now());
        assertThrows(CompletionAlreadyExistsException.class, () -> {
            completionController.completeHabit(createdHabitId, request);
        });
    }

    @Test
    @Order(9)
    void test9_futureCompletionShouldThrow400() {
        CompletionRequest request = new CompletionRequest(LocalDate.now().plusDays(2));
        assertThrows(InvalidCompletionException.class, () -> {
            completionController.completeHabit(createdHabitId, request);
        });
    }

    @Test
    @Order(10)
    void test10_nonExistingHabitShouldReturnDataNotFound() {
        ResponseEntity<?> response = habitController.getHabitById(999999L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data not found", response.getBody());
    }

    @Test
    @Order(11)
    void test11_deleteHabit() {
        ResponseEntity<Void> response = habitController.deleteHabit(createdHabitId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<?> getDeleted = habitController.getHabitById(createdHabitId);
        assertEquals(HttpStatus.OK, getDeleted.getStatusCode());
        assertEquals("Data not found", getDeleted.getBody());
    }
}
