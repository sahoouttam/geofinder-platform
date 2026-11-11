package com.geofinder.platform.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class OperatingHours {
    
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean closed;
    private boolean overnight;

    public static OperatingHours of(DayOfWeek day, LocalTime open, LocalTime close) {
        return OperatingHours.builder()
                    .dayOfWeek(day)
                    .openTime(open)
                    .closeTime(close)
                    .closed(false)
                    .overnight(!close.isAfter(open))
                    .build();
    }

    public static OperatingHours closedOn(DayOfWeek day) {
        return OperatingHours.builder()
                    .dayOfWeek(day)
                    .closed(true)
                    .build();
    }
}
