package com.studytracker.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class MonthlyAnalyticsResponse {
    
    private Long totalSessions;
    private Long totalHours;
    private Double averageSessionDuration;
    private List<WeeklyStats> weeklyStats;
    private Map<String, Long> hoursByFolder;
    private LocalDateTime monthStart;
    private LocalDateTime monthEnd;
    
    @Data
    public static class WeeklyStats {
        private String week;
        private Long sessions;
        private Long hours;
        
        public WeeklyStats(String week, Long sessions, Long hours) {
            this.week = week;
            this.sessions = sessions;
            this.hours = hours;
        }
    }
}
