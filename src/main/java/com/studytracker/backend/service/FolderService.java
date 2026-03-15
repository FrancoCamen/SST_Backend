package com.studytracker.backend.service;

import com.studytracker.backend.dto.FolderRequest;
import com.studytracker.backend.dto.FolderResponse;
import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.User;
import com.studytracker.backend.repository.FolderRepository;
import com.studytracker.backend.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FolderService {
    
    private final FolderRepository folderRepository;
    private final SessionRepository sessionRepository;
    
    public FolderResponse createFolder(FolderRequest request, User user) {
        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setDescription(request.getDescription());
        folder.setUser(user);
        
        folder = folderRepository.save(folder);
        
        return mapToFolderResponse(folder);
    }
    
    public List<FolderResponse> getUserFolders(User user) {
        List<Folder> folders = folderRepository.findByUser(user);
        
        return folders.stream()
                .map(this::mapToFolderResponse)
                .collect(Collectors.toList());
    }
    
    public FolderResponse getFolderById(Long id, User user) {
        Folder folder = folderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        
        return mapToFolderResponse(folder);
    }
    
    public FolderResponse updateFolder(Long id, FolderRequest request, User user) {
        Folder folder = folderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        
        folder.setName(request.getName());
        folder.setDescription(request.getDescription());
        
        folder = folderRepository.save(folder);
        
        return mapToFolderResponse(folder);
    }
    
    public void deleteFolder(Long id, User user) {
        if (!folderRepository.existsByIdAndUser(id, user)) {
            throw new RuntimeException("Folder not found");
        }
        
        folderRepository.deleteByIdAndUser(id, user);
    }
    
    private FolderResponse mapToFolderResponse(Folder folder) {
        Long totalHours = sessionRepository.totalMinutesByUserSince(
                folder.getUser(), 
                LocalDateTime.now().minusYears(10)
        ) != null ? sessionRepository.totalMinutesByUserSince(folder.getUser(), LocalDateTime.now().minusYears(10)) / 60 : 0L;
        
        Long sessionCount = sessionRepository.countSessionsByUserSince(
                folder.getUser(), 
                LocalDateTime.now().minusYears(10)
        );
        
        LocalDateTime lastSessionDate = sessionRepository
                .findByUser(folder.getUser())
                .stream()
                .map(session -> session.getStartTime())
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getDescription(),
                totalHours,
                sessionCount,
                lastSessionDate,
                folder.getCreatedAt()
        );
    }
}
