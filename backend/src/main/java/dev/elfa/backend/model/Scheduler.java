package dev.elfa.backend.model;

import dev.elfa.backend.model.scheduler.WeekDays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scheduler {
    private LocalTime scheduledTime;
    private Set<WeekDays> scheduledDays;
}
