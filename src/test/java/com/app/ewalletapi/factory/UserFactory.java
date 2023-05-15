package com.app.ewalletapi.factory;

import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.UserStatus;
import com.app.ewalletapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    private final UserRepository userRepository;

    @Autowired
    public UserFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createTestUser() {
        User user = new User();
        user.setName("John Doe");
        user.setPassword("password");
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return user;
    }
}
