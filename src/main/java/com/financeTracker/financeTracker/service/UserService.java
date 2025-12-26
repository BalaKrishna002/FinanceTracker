package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.DTO.NotificationRequest;
import com.financeTracker.financeTracker.DTO.UserDetailsResponseDTO;
import com.financeTracker.financeTracker.DTO.UserRegisterRequestDTO;
import com.financeTracker.financeTracker.Enums.NotificationChannelType;
import com.financeTracker.financeTracker.Enums.NotificationType;
import com.financeTracker.financeTracker.model.User;
import com.financeTracker.financeTracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AsyncNotificationService asyncNotificationService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AsyncNotificationService asyncNotificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.asyncNotificationService = asyncNotificationService;
    }

    public void register(UserRegisterRequestDTO request) {

        if (userRepository.existsByUsername(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName().strip())
                .username(request.getEmail().strip())
                .password(passwordEncoder.encode(request.getPassword()))
                .timezone(request.getTimezone())
                .currency(request.getCurrency())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        // ✅ ASYNC WELCOME EMAIL (NON-BLOCKING)
        asyncNotificationService.send(
                NotificationRequest.builder()
                        .channelType(NotificationChannelType.EMAIL)
                        .notificationType(NotificationType.USER_WELCOME)
                        .to(request.getEmail().strip())
                        .subject("Welcome to FTRACK 🎉")
                        .data(Map.of(
                                "fullName", user.getFullName()
                        ))
                        .build()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }

    // Security-context related logic is OK
    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

    public UserDetailsResponseDTO getUserDetails(Long id) {
        User user = getCurrentUser();
        return new UserDetailsResponseDTO(
                user.getFullName(),
                user.getUsername(),
                user.getTimezone(),
                user.getCurrency()
        );
    }
}

