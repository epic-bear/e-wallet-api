package com.app.ewalletapi.api;

import com.app.ewalletapi.api.request.UserRequest;
import com.app.ewalletapi.api.response.UserResponse;
import com.app.ewalletapi.model.User;
import com.app.ewalletapi.model.Wallet;
import com.app.ewalletapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        User user = userService.createUser(userRequest);
        UserResponse response = new UserResponse(user.getUserId(), user.getName(),
                user.getUserStatus(), user.getWallets().stream().map(Wallet::getWalletId).collect(Collectors.toSet()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) throws Exception {
        User user = userService.getUserById(userId);
        UserResponse response = new UserResponse(user.getUserId(), user.getName(),
                user.getUserStatus(), user.getWallets().stream().map(Wallet::getWalletId).collect(Collectors.toSet()));
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest userRequest) throws Exception {
        User user = userService.updateUser(userRequest);
        UserResponse response = new UserResponse(user.getUserId(), user.getName(),
                user.getUserStatus(), user.getWallets().stream().map(Wallet::getWalletId).collect(Collectors.toSet()));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reset")
    public ResponseEntity<String> resetStatus(@RequestBody UserRequest userRequest) {
        try {
            userService.resetStatus(userRequest);
            return ResponseEntity.ok("User unblocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
