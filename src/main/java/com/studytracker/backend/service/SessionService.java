package com.studytracker.backend.service;

import com.studytracker.backend.dto.SessionRequest;
import com.studytracker.backend.dto.SessionResponse;
import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.Session;
import com.studytracker.backend.model.Tag;
import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.repository.FolderRepository;
import com.studytracker.backend.repository.SessionRepository;
import com.studytracker.backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final FolderRepository folderRepository;
    private final TagRepository tagRepository;
    
    public SessionResponse createSession(SessionRequest request, AppUser user) {
        Folder folder = null;
        if (request.getFolderId() != null) {
            folder = folderRepository.findByIdAndUser(request.getFolderId(), user)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
        }
        
        Set<Tag> tags = processTags(request.getTags());
        
        Session session = new Session();
        session.setTitle(request.getTitle());
        session.setDescription(request.getDescription());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setFolder(folder);
        session.setUser(user);
        session.setTags(new ArrayList<>(tags));
        
        session = sessionRepository.save(session);
        
        return mapToSessionResponse(session);
    }
    
    public List<SessionResponse> getUserSessions(AppUser user) {
        List<Session> sessions = sessionRepository.findByUser(user);
        
        return sessions.stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }
    
    public List<SessionResponse> getUserSessionsByFolder(Long folderId, AppUser user) {
        Folder folder = folderRepository.findByIdAndUser(folderId, user)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        
        List<Session> sessions = sessionRepository.findByUserAndFolder(user, folder);
        
        return sessions.stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }
    
    public SessionResponse getSessionById(Long id, AppUser user) {
        Session session = sessionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        return mapToSessionResponse(session);
    }
    
    public SessionResponse updateSession(Long id, SessionRequest request, AppUser user) {
        Session session = sessionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        Folder folder = null;
        if (request.getFolderId() != null) {
            folder = folderRepository.findByIdAndUser(request.getFolderId(), user)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
        }
        
        Set<Tag> tags = processTags(request.getTags());
        
        session.setTitle(request.getTitle());
        session.setDescription(request.getDescription());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setFolder(folder);
        session.setTags(new ArrayList<>(tags));
        
        session = sessionRepository.save(session);
        
        return mapToSessionResponse(session);
    }
    
    public void deleteSession(Long id, AppUser user) {
        if (!sessionRepository.existsById(id)) {
            throw new RuntimeException("Session not found");
        }
        
        Session session = sessionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        sessionRepository.delete(session);
    }
    
    private Set<Tag> processTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Set.of();
        }
        
        return tagNames.stream()
                .distinct()
                .map(tagName -> {
                    Tag tag = tagRepository.findByName(tagName).orElse(null);
                    if (tag == null) {
                        tag = new Tag(tagName);
                        tag = tagRepository.save(tag);
                    }
                    return tag;
                })
                .collect(Collectors.toSet());
    }
    
    private SessionResponse mapToSessionResponse(Session session) {
        List<String> tagNames = session.getTags() != null 
                ? session.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList())
                : List.of();
        
        String folderName = session.getFolder() != null ? session.getFolder().getName() : null;
        Long folderId = session.getFolder() != null ? session.getFolder().getId() : null;
        
        return new SessionResponse(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getStartTime(),
                session.getEndTime(),
                session.getDurationMinutes(),
                folderId,
                folderName,
                tagNames,
                session.getCreatedAt()
        );
    }
}
