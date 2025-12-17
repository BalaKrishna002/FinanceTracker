package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.model.User;
import com.financeTracker.financeTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = Long.parseLong(authentication.getName());

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

