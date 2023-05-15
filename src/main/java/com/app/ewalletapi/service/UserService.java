package com.app.ewalletapi.service;

import com.app.ewalletapi.api.request.UserRequest;
import com.app.ewalletapi.model.Transaction;
import com.app.ewalletapi.model.UserStatus;
import com.app.ewalletapi.model.Wallet;
import com.app.ewalletapi.repository.UserRepository;
import com.app.ewalletapi.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Autowired
    public UserService(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    public User createUser(UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setPassword(userRequest.getPassword());
        user.setUserStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    public User getUserById(long userId) throws Exception {
        return userRepository.findById(userId).orElseThrow(() -> new Exception(messageSource.getMessage("UserNotFound", new Object[]{userId}, Locale.getDefault())));
    }

    public User updateUser(UserRequest userRequest) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userRequest.getUserId());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            String name = userRequest.getName();
            if (name != null) {
                existingUser.setName(name);
            }
            String password = userRequest.getPassword();
            if (password != null) {
                existingUser.setPassword(password);
            }
            userRepository.save(existingUser);
            return existingUser;
        } else {
            throw new Exception(messageSource.getMessage("UserNotFound", new Object[]{userRequest.getUserId()}, Locale.getDefault()));
        }
    }

    public void deleteUser(long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception(messageSource.getMessage("UserNotFound", new Object[]{userId}, Locale.getDefault())));
        userRepository.delete(user);
    }

    public void changeUserStatus(User user, UserStatus userStatus) {
        user.setUserStatus(userStatus);
        userRepository.save(user);
    }

    public void unblockAllUsers() {
        Set<User> blockedUsers = userRepository.findAllByUserStatus(UserStatus.BLOCKED);
        for (User user : blockedUsers) {
            changeUserStatus(user, UserStatus.SUSPICIOUS);
        }
    }

    public void resetStatus(UserRequest userRequest) throws Exception {
        User user = getUserById(userRequest.getUserId());
        checkUserCredentials(user, userRequest.getName(), userRequest.getPassword());
        changeUserStatus(user, UserStatus.SUSPICIOUS);
    }

    public void checkUserCredentials(User user, String name, String password) throws Exception {
        if (!user.getName().equals(name) || !user.getPassword().equals(password)) {
            throw new Exception("Invalid credentials");
        }
    }

    public void checkUserAccess(User user) throws Exception {
        if (user.getUserStatus() == UserStatus.BLOCKED) {
            throw new Exception("This user is blocked");
        }
    }

    public int countTransactionsWithinHour(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        int count = 0;
        for (Wallet wallet : user.getWallets()) {
            for (Transaction transaction : wallet.getTransactions()) {
                LocalDateTime transactionTime = transaction.getDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                if (transactionTime.isAfter(oneHourAgo) && transactionTime.isBefore(now)) {
                    count++;
                }
            }
        }
        return count;
    }
}
