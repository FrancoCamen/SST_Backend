package com.studytracker.backend.service;

import com.studytracker.backend.dto.MonthlyAnalyticsResponse;
import com.studytracker.backend.dto.WeeklyAnalyticsResponse;
import com.studytracker.backend.model.Session;
import com.studytracker.backend.model.User;
import com.studytracker.backend.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final SessionRepository sessionRepository;
    
    public WeeklyAnalyticsResponse getWeeklyAnalytics(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime weekEnd = now;
        
        List<Session> sessions = sessionRepository.findByUserAndDateRange(user, weekStart, weekEnd);
        
        return buildWeeklyResponse(sessions, weekStart, weekEnd);
    }
    
    public MonthlyAnalyticsResponse getMonthlyAnalytics(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.minusMonths(1);
        LocalDateTime monthEnd = now;
        
        List<Session> sessions = sessionRepository.findByUserAndDateRange(user, monthStart, monthEnd);
        
        return buildMonthlyResponse(sessions, monthStart, monthEnd);
    }
    
    public Map<String, Long> getHoursByFolder(User user) {
        List<Session> sessions = sessionRepository.findByUser(user);
        
        return sessions.stream()
                .filter(session -> session.getFolder() != null)
                .collect(Collectors.groupingBy(
                        session -> session.getFolder().getName(),
                        Collectors.summingLong(Session::getDurationMinutes)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / 60
                ));
    }
    
    public Map<Integer, Long> getProductivityHours(User user) {
        List<Session> sessions = sessionRepository.findByUser(user);
        
        return sessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getStartTime().getHour(),
                        Collectors.summingLong(Session::getDurationMinutes)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / 60
                ));
    }
    
    private WeeklyAnalyticsResponse buildWeeklyResponse(List<Session> sessions, LocalDateTime weekStart, LocalDateTime weekEnd) {
        long totalSessions = sessions.size();
        long totalMinutes = sessions.stream().mapToLong(Session::getDurationMinutes).sum();
        long totalHours = totalMinutes / 60;
        double averageDuration = totalSessions > 0 ? (double) totalMinutes / totalSessions : 0.0;
        
        Map<String, Long> hoursByFolder = sessions.stream()
                .filter(session -> session.getFolder() != null)
                .collect(Collectors.groupingBy(
                        session -> session.getFolder().getName(),
                        Collectors.summingLong(Session::getDurationMinutes)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / 60
                ));
        
        List<WeeklyAnalyticsResponse.DailyStats> dailyStats = groupSessionsByDay(sessions);
        
        WeeklyAnalyticsResponse response = new WeeklyAnalyticsResponse();
        response.setTotalSessions(totalSessions);
        response.setTotalHours(totalHours);
        response.setAverageSessionDuration(averageDuration);
        response.setDailyStats(dailyStats);
        response.setHoursByFolder(hoursByFolder);
        response.setWeekStart(weekStart);
        response.setWeekEnd(weekEnd);
        
        return response;
    }
    
    private MonthlyAnalyticsResponse buildMonthlyResponse(List<Session> sessions, LocalDateTime monthStart, LocalDateTime monthEnd) {
        long totalSessions = sessions.size();
        long totalMinutes = sessions.stream().mapToLong(Session::getDurationMinutes).sum();
        long totalHours = totalMinutes / 60;
        double averageDuration = totalSessions > 0 ? (double) totalMinutes / totalSessions : 0.0;
        
        Map<String, Long> hoursByFolder = sessions.stream()
                .filter(session -> session.getFolder() != null)
                .collect(Collectors.groupingBy(
                        session -> session.getFolder().getName(),
                        Collectors.summingLong(Session::getDurationMinutes)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / 60
                ));
        
        List<MonthlyAnalyticsResponse.WeeklyStats> weeklyStats = groupSessionsByWeek(sessions);
        
        MonthlyAnalyticsResponse response = new MonthlyAnalyticsResponse();
        response.setTotalSessions(totalSessions);
        response.setTotalHours(totalHours);
        response.setAverageSessionDuration(averageDuration);
        response.setWeeklyStats(weeklyStats);
        response.setHoursByFolder(hoursByFolder);
        response.setMonthStart(monthStart);
        response.setMonthEnd(monthEnd);
        
        return response;
    }
    
    private List<WeeklyAnalyticsResponse.DailyStats> groupSessionsByDay(List<Session> sessions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return sessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getStartTime().format(formatter),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                daySessions -> {
                                    long dayMinutes = daySessions.stream()
                                            .mapToLong(Session::getDurationMinutes)
                                            .sum();
                                    return new WeeklyAnalyticsResponse.DailyStats(
                                            daySessions.get(0).getStartTime().format(formatter),
                                            (long) daySessions.size(),
                                            dayMinutes / 60
                                    );
                                }
                        )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(WeeklyAnalyticsResponse.DailyStats::getDate))
                .collect(Collectors.toList());
    }
    
    private List<MonthlyAnalyticsResponse.WeeklyStats> groupSessionsByWeek(List<Session> sessions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-'W'ww");
        
        return sessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getStartTime().format(formatter),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                weekSessions -> {
                                    long weekMinutes = weekSessions.stream()
                                            .mapToLong(Session::getDurationMinutes)
                                            .sum();
                                    return new MonthlyAnalyticsResponse.WeeklyStats(
                                            weekSessions.get(0).getStartTime().format(formatter),
                                            (long) weekSessions.size(),
                                            weekMinutes / 60
                                    );
                                }
                        )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(MonthlyAnalyticsResponse.WeeklyStats::getWeek))
                .collect(Collectors.toList());
    }
}
