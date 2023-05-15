package com.app.ewalletapi.job;

import com.app.ewalletapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserUnblockScheduler {

    private UserService userService;

    @Autowired
    public UserUnblockScheduler(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void unblockUsers() {
        userService.unblockAllUsers();
    }
}
