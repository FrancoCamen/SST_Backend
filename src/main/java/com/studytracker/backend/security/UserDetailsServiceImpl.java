package com.studytracker.backend.security;

import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.repository.UserRepository; // O AppUserRepository si lo renombraste
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository; 
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Aquí envolvemos tu AppUser en el CustomUserDetails que acabamos de crear
        return new CustomUserDetails(appUser);
    }
}
