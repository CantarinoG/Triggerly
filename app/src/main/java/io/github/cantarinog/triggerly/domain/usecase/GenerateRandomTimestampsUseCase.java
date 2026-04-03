package io.github.cantarinog.triggerly.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateRandomTimestampsUseCase {

    private final Random random = new Random();

    public List<LocalDateTime> execute(LocalDate targetDate, LocalTime startTime, LocalTime endTime, int count) {
        List<LocalDateTime> timestamps = new ArrayList<>();
        if (count <= 0) return timestamps;

        long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        if (totalMinutes <= 0) {
            totalMinutes += 24 * 60;
        }

        long intervalMinutes = totalMinutes / count;

        for (int i = 0; i < count; i++) {
            long windowStartOffset = i * intervalMinutes;
            
            long randomMinuteInWindow = windowStartOffset + (long) (random.nextDouble() * intervalMinutes);
            
            LocalTime generatedTime = startTime.plusMinutes(randomMinuteInWindow);
            LocalDateTime generatedDateTime = LocalDateTime.of(targetDate, generatedTime);
            
            if (generatedTime.isBefore(startTime) && totalMinutes > (24 * 60 - 1)) {
                generatedDateTime = generatedDateTime.plusDays(1);
            }
            
            timestamps.add(generatedDateTime);
        }

        return timestamps;
    }
}
