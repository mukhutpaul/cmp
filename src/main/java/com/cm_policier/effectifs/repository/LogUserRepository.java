package com.cm_policier.effectifs.repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.LogUser;


public interface LogUserRepository extends JpaRepository<LogUser, UUID> {

}
