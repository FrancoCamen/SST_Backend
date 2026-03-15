package com.studytracker.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class WeeklyAnalyticsResponse {
    
    private Long totalSessions;
    private Long totalHours;
    private Double averageSessionDuration;
    private List<DailyStats> dailyStats;
    private Map<String, Long> hoursByFolder;
    private LocalDateTime weekStart;
    private LocalDateTime weekEnd;
    
    @Data
    public static class DailyStats {
        private String date;
        private Long sessions;
        private Long hours;
        
        public DailyStats(String date, Long sessions, Long hours) {
            this.date = date;
            this.sessions = sessions;
            this.hours = hours;
        }
    }
}
