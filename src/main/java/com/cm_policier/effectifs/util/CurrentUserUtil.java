package com.cm_policier.effectifs.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class CurrentUserUtil {

    public static String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        if (principal instanceof String username) {

            // 🔥 IMPORTANT: filtrer anonymousUser
            if ("anonymousUser".equals(username)) {
                return null;
            }

            return username;
        }

        return null;
    }
}