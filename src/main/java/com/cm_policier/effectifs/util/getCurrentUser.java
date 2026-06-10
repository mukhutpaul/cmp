package com.cm_policier.effectifs.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cm_policier.effectifs.model.User;

public class getCurrentUser {

    public static  User getCurrentUser() {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null ||
        !authentication.isAuthenticated()) {
        return null;
    }

    return (User) authentication.getPrincipal();
}

}
