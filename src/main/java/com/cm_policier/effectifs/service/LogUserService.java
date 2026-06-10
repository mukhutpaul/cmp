package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.LogUserResponse;
import com.cm_policier.effectifs.model.LogUser;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.LogUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogUserService {


    private final LogUserRepository logUserRepository;

    public void saveLog(User user, String action) {

        LogUser log = LogUser.builder()
                .user(user)
                .action(action)
                .build();

        logUserRepository.save(log);
    }

   public List<LogUserResponse> getAllLogs() {
    return logUserRepository.findAll(
            Sort.by(Sort.Direction.DESC, "createdAt"))
            .stream()
            .map(log -> LogUserResponse.builder()
                    .id(log.getId())
                    .username(log.getUser().getUsername())
                    .noms(log.getUser().getNoms())
                    .action(log.getAction())
                    .createdAt(log.getCreatedAt())
                    .build())
            .toList();
}
}