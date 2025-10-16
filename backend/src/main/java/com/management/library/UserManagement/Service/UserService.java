package com.management.library.UserManagement.Service;

import com.management.library.UserManagement.Dto.*;
import com.management.library.UserManagement.Entity.User;
import com.management.library.UserManagement.Exception.*;
import com.management.library.UserManagement.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFromAddress;

    // Manual constructor
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with username: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setStatus(User.UserStatus.ACTIVATED);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        // Note: Member profile will be created only when user explicitly becomes a member
        // This keeps user signup separate from member registration

        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse loginUser(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // Find user by username
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            log.warn("Login failed - Username not found: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = optionalUser.get();

        // Check if user account is activated
        if (user.getStatus() == User.UserStatus.DEACTIVATED) {
            log.warn("Login failed - Account deactivated for username: {}", request.getUsername());
            throw new AccountDeactivatedException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - Invalid password for username: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        log.info("User logged in successfully with username: {}", request.getUsername());
        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserById(String id) {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return UserResponse.fromEntity(user);
    }

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByStatus(User.UserStatus status) {
        log.info("Fetching users with status: {}", status);

        List<User> users = userRepository.findByStatus(status);
        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UserResponse> searchUsers(String query) {
        log.info("Searching users with query: {}", query);

        List<User> users = userRepository.findBySearchQuery(query);
        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(String id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Check if username is being changed and if it already exists
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // Check if email is being changed and if it already exists
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // Update other fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return UserResponse.fromEntity(updatedUser);
    }

    public void changePassword(String id, ChangePasswordRequest request) {
        log.info("Changing password for user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Password changed successfully for user with ID: {}", id);
    }

    public void activateUser(String id) {
        log.info("Activating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setStatus(User.UserStatus.ACTIVATED);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("User activated successfully with ID: {}", id);
    }

    public void deactivateUser(String id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setStatus(User.UserStatus.DEACTIVATED);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("User deactivated successfully with ID: {}", id);
    }

    public void deleteUser(String id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", id);
    }

    public long getUserCountByStatus(User.UserStatus status) {
        return userRepository.countByStatus(status);
    }

    public void requestPasswordReset(ForgotPasswordRequest request) {
        log.info("Requesting password reset for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        // Generate a 6-digit numeric token
        SecureRandom secureRandom = new SecureRandom();
        String token = String.format("%06d", secureRandom.nextInt(1_000_000));
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(2));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Send token via email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFromAddress);
            message.setTo(user.getEmail());
            message.setSubject("Library - Password Reset Token");
            message.setText("Use this token to reset your password: " + token + " This token expires in 2 minutes.");
            mailSender.send(message);
            log.info("Password reset token email sent to: {}", user.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send reset email: {}", ex.getMessage());
            // Still expose generic response to caller; admins can inspect logs.
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password using token");

        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}