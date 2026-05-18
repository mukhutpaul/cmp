package com.cm_policier.effectifs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cm_policier.effectifs.dto.UpdateUserRequest;
import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Profile;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.DetailEquipeRepository;
import com.cm_policier.effectifs.repository.DetailUniteRepository;
import com.cm_policier.effectifs.repository.ProfileRepository;
import com.cm_policier.effectifs.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DetailEquipeRepository detailEquipeRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private DetailUniteRepository detailUniteRepository;

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
        List<User> users = userRepository.findAll();

        for (User u : users) {
            System.out.println("===== USER DEBUG =====");
            System.out.println("ID: " + u.getId());
            System.out.println("USERNAME: " + u.getUsername());
            System.out.println("PASSWORD: " + u.getPassword());
            System.out.println("EMAIL: " + u.getEmail());
        }

        return users;
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

    public List<DetailUnite> getUnitesByUserId(Long userId) {
        return detailUniteRepository.findByUser_Id(userId);
    }

    // =========================
    // DELETE USER
    // =========================
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public User findFullById(Long id) {
        return userRepository.findFullById(id);
    }

    public List<User> findUsersByEquipe(Long equipeId) {

        List<DetailEquipe> details = detailEquipeRepository.findByEquipe_Id(equipeId);

        List<User> users = details.stream()
                .map(DetailEquipe::getUser)
                .toList();

        users.forEach(u -> u.setPassword(null));

        return users;
    }

    public List<User> findAllByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return userRepository.findAllByIds(ids);
    }

}