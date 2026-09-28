// HabitForge - Simple, Clean Vanilla JavaScript

const API_BASE = "/api/habits";

// State
let habits = [];
let calendarHabitId = null;
let calendarHabitName = "";
let calendarYear = new Date().getFullYear();
let calendarMonth = new Date().getMonth() + 1; // 1-12

// Weekday mapping (1 = Monday, 7 = Sunday)
const WEEKDAY_NAMES = {
    "1": "Mon",
    "2": "Tue",
    "3": "Wed",
    "4": "Thu",
    "5": "Fri",
    "6": "Sat",
    "7": "Sun"
};

const MONTH_NAMES = [
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
];

// Initialize on DOM load
document.addEventListener("DOMContentLoaded", () => {
    loadHabits();
});

// Format today's date as YYYY-MM-DD
function getTodayString() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

// --------------------------------------------------
// API: Load All Habits & Dashboard State
// --------------------------------------------------
async function loadHabits() {
    try {
        const response = await fetch(API_BASE);
        if (!response.ok) {
            throw new Error(`Failed to fetch habits (Status: ${response.status})`);
        }

        const rawHabits = await response.json();
        const todayStr = getTodayString();
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth() + 1;

        // Fetch streak and today's status for each habit
        habits = await Promise.all(
            rawHabits.map(async (habit) => {
                let streakData = { currentStreak: 0, bestStreak: 0 };
                let todayStatus = "REST_DAY"; // COMPLETED, NEEDS_COMPLETION, REST_DAY

                try {
                    const streakRes = await fetch(`${API_BASE}/${habit.id}/streak`);
                    if (streakRes.ok) {
                        streakData = await streakRes.json();
                    }
                } catch (e) {
                    console.error("Error loading streak for habit", habit.id, e);
                }

                try {
                    const calRes = await fetch(`${API_BASE}/${habit.id}/calendar?year=${year}&month=${month}`);
                    if (calRes.ok) {
                        const days = await calRes.json();
                        const todayRecord = days.find((d) => d.date === todayStr);
                        if (todayRecord) {
                            if (todayRecord.completed) {
                                todayStatus = "COMPLETED";
                            } else if (todayRecord.required) {
                                todayStatus = "NEEDS_COMPLETION";
                            } else {
                                todayStatus = "REST_DAY";
                            }
                        }
                    }
                } catch (e) {
                    console.error("Error loading calendar for habit", habit.id, e);
                }

                return {
                    ...habit,
                    currentStreak: streakData.currentStreak,
                    bestStreak: streakData.bestStreak,
                    todayStatus: todayStatus
                };
            })
        );

        // Update Dashboard Summary Stats
        updateStats();

        // Show Reminders
        showReminder();

        // Render Habit Cards
        renderHabits();
    } catch (error) {
        console.error("Error in loadHabits:", error);
        showAlert(`Unable to load habits: ${error.message}`, "danger");
    }
}

// --------------------------------------------------
// Render Dashboard Stats
// --------------------------------------------------
function updateStats() {
    const totalHabits = habits.length;
    const completedCount = habits.filter((h) => h.todayStatus === "COMPLETED").length;
    const attentionCount = habits.filter((h) => h.todayStatus === "NEEDS_COMPLETION").length;

    document.getElementById("statTotalHabits").textContent = totalHabits;
    document.getElementById("statTodayCompleted").textContent = completedCount;
    document.getElementById("statRequiringAttention").textContent = attentionCount;
}

// --------------------------------------------------
// Reminders Section
// --------------------------------------------------
function showReminder() {
    const reminderSection = document.getElementById("reminderSection");
    const reminderList = document.getElementById("reminderList");
    reminderList.innerHTML = "";

    const needingCompletion = habits.filter((h) => h.todayStatus === "NEEDS_COMPLETION");

    if (needingCompletion.length === 0) {
        reminderSection.classList.add("hidden");
        return;
    }

    needingCompletion.forEach((habit) => {
        const li = document.createElement("li");
        li.textContent = `Reminder: Don't forget to complete "${habit.name}" today.`;
        reminderList.appendChild(li);
    });

    reminderSection.classList.remove("hidden");
}

// --------------------------------------------------
// Render Habit Cards
// --------------------------------------------------
function renderHabits() {
    const container = document.getElementById("habitsList");
    container.innerHTML = "";

    if (habits.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>No habits found</h3>
                <p>Click the <strong>+ Add Habit</strong> button above to create your first habit tracker!</p>
            </div>
        `;
        return;
    }

    habits.forEach((habit) => {
        const card = document.createElement("div");
        card.className = "habit-card";

        // Frequency Label
        let frequencyText = "Daily";
        if (habit.frequencyType === "SPECIFIC_DAYS" && habit.targetDays) {
            const dayNames = habit.targetDays
                .split(",")
                .map((d) => WEEKDAY_NAMES[d.trim()] || d.trim())
                .join(", ");
            frequencyText = `Specific Days (${dayNames})`;
        }

        // Today's Status Badge & Button state
        let statusBadgeHtml = "";
        let doneDisabled = false;
        let doneBtnText = "DONE";

        if (habit.todayStatus === "COMPLETED") {
            statusBadgeHtml = `<span class="status-badge status-completed">✓ Completed Today</span>`;
            doneDisabled = true;
            doneBtnText = "✓ Completed";
        } else if (habit.todayStatus === "NEEDS_COMPLETION") {
            statusBadgeHtml = `<span class="status-badge status-needs-completion">Needs Completion</span>`;
            doneDisabled = false;
            doneBtnText = "DONE";
        } else {
            statusBadgeHtml = `<span class="status-badge status-rest">Rest Day</span>`;
            doneDisabled = true;
            doneBtnText = "Rest Day";
        }

        card.innerHTML = `
            <div>
                <div class="habit-card-header">
                    <h3 class="habit-title">${escapeHtml(habit.name)}</h3>
                    ${statusBadgeHtml}
                </div>
                <p class="habit-desc">${escapeHtml(habit.description || "No description provided.")}</p>

                <div class="habit-meta">
                    <div class="meta-row">
                        <span class="meta-label">Frequency:</span>
                        <span class="meta-value">${escapeHtml(frequencyText)}</span>
                    </div>
                    <div class="meta-row">
                        <span class="meta-label">Current Streak:</span>
                        <span class="meta-value"><strong>${habit.currentStreak}</strong> days</span>
                    </div>
                    <div class="meta-row">
                        <span class="meta-label">Best Streak:</span>
                        <span class="meta-value"><strong>${habit.bestStreak}</strong> days</span>
                    </div>
                </div>
            </div>

            <div class="habit-card-actions">
                <div class="card-actions-top">
                    <button class="btn btn-success" ${doneDisabled ? "disabled" : ""} onclick="completeHabit(${habit.id})">
                        ${doneBtnText}
                    </button>
                </div>
                <div class="card-actions-bottom">
                    <button class="btn btn-secondary btn-sm" onclick="openCalendarModal(${habit.id}, '${escapeJsString(habit.name)}', ${habit.currentStreak}, ${habit.bestStreak})">
                        View Calendar
                    </button>
                    <button class="btn btn-secondary btn-sm" onclick="openEditHabitModal(${habit.id})">
                        Edit
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="deleteHabit(${habit.id})">
                        Delete
                    </button>
                </div>
            </div>
        `;

        container.appendChild(card);
    });
}

// --------------------------------------------------
// Complete Habit (DONE Action)
// --------------------------------------------------
async function completeHabit(habitId) {
    try {
        const todayStr = getTodayString();
        const response = await fetch(`${API_BASE}/${habitId}/complete`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ completionDate: todayStr })
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            const msg = errData.message || `Status: ${response.status}`;
            throw new Error(msg);
        }

        showAlert("Habit completed successfully!", "success");
        await loadHabits();
    } catch (error) {
        console.error("Error in completeHabit:", error);
        showAlert(`Unable to complete habit: ${error.message}`, "danger");
    }
}

// --------------------------------------------------
// Add / Edit Modal & Form Handling
// --------------------------------------------------
function openAddHabitModal() {
    document.getElementById("modalTitle").textContent = "Add Habit";
    document.getElementById("habitId").value = "";
    document.getElementById("habitName").value = "";
    document.getElementById("habitDescription").value = "";
    document.getElementById("habitFrequency").value = "DAILY";

    // Uncheck all weekday checkboxes
    document.querySelectorAll('input[name="targetDay"]').forEach((cb) => (cb.checked = false));

    toggleWeekdaySelection();
    document.getElementById("habitModal").classList.remove("hidden");
    document.getElementById("habitName").focus();
}

function openEditHabitModal(habitId) {
    const habit = habits.find((h) => h.id === habitId);
    if (!habit) return;

    document.getElementById("modalTitle").textContent = "Edit Habit";
    document.getElementById("habitId").value = habit.id;
    document.getElementById("habitName").value = habit.name;
    document.getElementById("habitDescription").value = habit.description || "";
    document.getElementById("habitFrequency").value = habit.frequencyType;

    // Check target days
    const activeDays = habit.targetDays ? habit.targetDays.split(",").map((s) => s.trim()) : [];
    document.querySelectorAll('input[name="targetDay"]').forEach((cb) => {
        cb.checked = activeDays.includes(cb.value);
    });

    toggleWeekdaySelection();
    document.getElementById("habitModal").classList.remove("hidden");
    document.getElementById("habitName").focus();
}

function closeHabitModal() {
    document.getElementById("habitModal").classList.add("hidden");
}

function toggleWeekdaySelection() {
    const freq = document.getElementById("habitFrequency").value;
    const weekdayGroup = document.getElementById("weekdayGroup");
    if (freq === "SPECIFIC_DAYS") {
        weekdayGroup.classList.remove("hidden");
    } else {
        weekdayGroup.classList.add("hidden");
    }
}

async function handleHabitFormSubmit(event) {
    event.preventDefault();

    const id = document.getElementById("habitId").value;
    const name = document.getElementById("habitName").value.trim();
    const description = document.getElementById("habitDescription").value.trim();
    const frequencyType = document.getElementById("habitFrequency").value;

    let targetDays = null;
    if (frequencyType === "SPECIFIC_DAYS") {
        const checkedDays = Array.from(document.querySelectorAll('input[name="targetDay"]:checked')).map(
            (cb) => cb.value
        );
        if (checkedDays.length === 0) {
            showAlert("Please select at least one required weekday for Specific Days.", "danger");
            return;
        }
        targetDays = checkedDays.join(",");
    }

    const payload = {
        name,
        description,
        frequencyType,
        targetDays
    };

    try {
        let response;
        if (id) {
            // Update
            response = await fetch(`${API_BASE}/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        } else {
            // Create
            response = await fetch(API_BASE, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        }

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.message || `Request failed with status ${response.status}`);
        }

        closeHabitModal();
        showAlert(id ? "Habit updated successfully!" : "Habit created successfully!", "success");
        await loadHabits();
    } catch (error) {
        console.error("Error saving habit:", error);
        showAlert(`Unable to save habit: ${error.message}`, "danger");
    }
}

// --------------------------------------------------
// Delete Habit
// --------------------------------------------------
async function deleteHabit(habitId) {
    const habit = habits.find((h) => h.id === habitId);
    const habitName = habit ? habit.name : "this habit";

    if (!confirm(`Are you sure you want to delete "${habitName}"? This will delete all its streaks and logs.`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/${habitId}`, {
            method: "DELETE"
        });

        if (!response.ok && response.status !== 204) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.message || `Delete failed with status ${response.status}`);
        }

        showAlert(`Habit "${habitName}" deleted successfully!`, "success");
        await loadHabits();
    } catch (error) {
        console.error("Error deleting habit:", error);
        showAlert(`Unable to delete habit: ${error.message}`, "danger");
    }
}

// --------------------------------------------------
// Monthly Calendar Modal & View
// --------------------------------------------------
function openCalendarModal(habitId, habitName, currentStreak, bestStreak) {
    calendarHabitId = habitId;
    calendarHabitName = habitName;

    const now = new Date();
    calendarYear = now.getFullYear();
    calendarMonth = now.getMonth() + 1;

    document.getElementById("calendarHabitName").textContent = habitName;
    document.getElementById("calCurrentStreak").textContent = currentStreak;
    document.getElementById("calBestStreak").textContent = bestStreak;

    loadCalendar(calendarHabitId, calendarYear, calendarMonth);
    document.getElementById("calendarModal").classList.remove("hidden");
}

function closeCalendarModal() {
    document.getElementById("calendarModal").classList.add("hidden");
}

function changeMonth(delta) {
    calendarMonth += delta;
    if (calendarMonth > 12) {
        calendarMonth = 1;
        calendarYear += 1;
    } else if (calendarMonth < 1) {
        calendarMonth = 12;
        calendarYear -= 1;
    }
    loadCalendar(calendarHabitId, calendarYear, calendarMonth);
}

async function loadCalendar(habitId, year, month) {
    const monthTitle = `${MONTH_NAMES[month - 1]} ${year}`;
    document.getElementById("calendarMonthTitle").textContent = monthTitle;
    const grid = document.getElementById("calendarGrid");
    grid.innerHTML = '<div class="loading-text">Loading calendar...</div>';

    try {
        // Fetch streak updates
        const streakRes = await fetch(`${API_BASE}/${habitId}/streak`);
        if (streakRes.ok) {
            const streakData = await streakRes.json();
            document.getElementById("calCurrentStreak").textContent = streakData.currentStreak;
            document.getElementById("calBestStreak").textContent = streakData.bestStreak;
        }

        // Fetch calendar days
        const calRes = await fetch(`${API_BASE}/${habitId}/calendar?year=${year}&month=${month}`);
        if (!calRes.ok) {
            throw new Error(`Failed to load calendar (Status: ${calRes.status})`);
        }

        const days = await calRes.json();
        renderCalendarGrid(days, year, month);
    } catch (error) {
        console.error("Error loading calendar:", error);
        grid.innerHTML = `<div class="loading-text text-danger">Error: ${error.message}</div>`;
    }
}

function renderCalendarGrid(days, year, month) {
    const grid = document.getElementById("calendarGrid");
    grid.innerHTML = "";

    const todayStr = getTodayString();
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // Calculate day of week offset for the 1st of the month
    // In JavaScript, Date.getDay(): 0 = Sun, 1 = Mon, ..., 6 = Sat
    // We want Monday = 0, Tuesday = 1, ..., Sunday = 6
    const firstDayDate = new Date(year, month - 1, 1);
    const dayOfWeek = firstDayDate.getDay();
    const mondayOffset = (dayOfWeek + 6) % 7; // 0 for Mon, 6 for Sun

    // Empty cells before the 1st
    for (let i = 0; i < mondayOffset; i++) {
        const emptyCell = document.createElement("div");
        emptyCell.className = "calendar-day-cell day-empty";
        grid.appendChild(emptyCell);
    }

    // Days of month
    days.forEach((dayRecord) => {
        const cell = document.createElement("div");
        cell.className = "calendar-day-cell";

        const dayNum = parseInt(dayRecord.date.split("-")[2], 10);
        cell.textContent = dayNum;

        const cellDate = new Date(dayRecord.date + "T00:00:00");
        const isToday = dayRecord.date === todayStr;
        const isPastOrToday = cellDate <= today;

        if (isToday) {
            cell.classList.add("day-today");
            cell.title = "Today";
        }

        if (dayRecord.completed) {
            cell.classList.add("day-completed");
            cell.title = `${dayRecord.date}: Completed`;
        } else if (dayRecord.required) {
            if (isPastOrToday) {
                cell.classList.add("day-missed");
                cell.title = isToday ? `${dayRecord.date}: Needs completion` : `${dayRecord.date}: Missed`;
            } else {
                cell.classList.add("day-rest");
                cell.title = `${dayRecord.date}: Required (Upcoming)`;
            }
        } else {
            cell.classList.add("day-rest");
            cell.title = `${dayRecord.date}: Rest day`;
        }

        grid.appendChild(cell);
    });
}

// --------------------------------------------------
// Alert Banner Utilities
// --------------------------------------------------
function showAlert(message, type = "danger") {
    const alertBox = document.getElementById("alertBox");
    const alertMessage = document.getElementById("alertMessage");

    alertBox.className = `alert alert-${type}`;
    alertMessage.textContent = message;
    alertBox.classList.remove("hidden");

    // Auto dismiss success messages after 4 seconds
    if (type === "success") {
        setTimeout(() => {
            alertBox.classList.add("hidden");
        }, 4000);
    }
}

function dismissAlert() {
    document.getElementById("alertBox").classList.add("hidden");
}

// --------------------------------------------------
// String Escaping Utilities
// --------------------------------------------------
function escapeHtml(text) {
    if (!text) return "";
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function escapeJsString(text) {
    if (!text) return "";
    return text.replace(/'/g, "\\'").replace(/"/g, '\\"');
}
