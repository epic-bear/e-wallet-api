package com.app.ewalletapi.repository;

import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Set<User> findAllByUserStatus(UserStatus userStatus);
}
