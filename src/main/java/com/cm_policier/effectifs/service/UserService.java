package com.cm_policier.effectifs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cm_policier.effectifs.dto.UpdateUserRequest;
import com.cm_policier.effectifs.model.Profile;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.ProfileRepository;
import com.cm_policier.effectifs.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // REGISTER
    // =========================
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // =========================
    // LOGIN
    // =========================
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    // =========================
    // GET ALL USERS
    // =========================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================
    // GET USER BY ID
    // =========================
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // UPDATE USER (PATCH STYLE)
    // =========================

    @Transactional
    public User updateUser(Long id, UpdateUserRequest request) {

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null)
            existing.setUsername(request.getUsername());

        if (request.getEmail() != null)
            existing.setEmail(request.getEmail());

        if (request.getNoms() != null)
            existing.setNoms(request.getNoms());

        if (request.getPassword() != null && !request.getPassword().isBlank())
            existing.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getProfileId() != null) {

            Profile profile = profileRepository.getReferenceById(request.getProfileId());

            existing.setProfile(profile);
        }

        return userRepository.save(existing);
    }

    // =========================
    // DELETE USER
    // =========================
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }
}