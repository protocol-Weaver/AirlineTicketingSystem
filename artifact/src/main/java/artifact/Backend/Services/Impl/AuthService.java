package artifact.Backend.Services.Impl;

import artifact.Backend.Models.DTO.LoginRequest;
import artifact.Backend.Models.DTO.RegisterRequest;
import artifact.Backend.Notification.NotificationFactory;
import artifact.Backend.Notification.NotificationManager;
import artifact.Backend.Models.Notification;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.User;
import artifact.Backend.Repositories.Interfaces.IUserRepository;
import artifact.Backend.Services.Interfaces.IAuthService;
import artifact.Backend.Tags.UserRole;
import artifact.Backend.UserSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Service implementation for Authentication and Authorization.
 * Handles standard Login, OTP-based Registration, and Google OAuth integration.
 */
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final UserSession userSession = UserSession.getInstance();
    private final NotificationManager notificationManager = NotificationManager.getInstance();
    
    // In-memory storage for OTPs and temporary user data during the 2-step registration process
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, User> tempRegistrations = new HashMap<>();

    public AuthService(IUserRepository userRepo) {
        this.userRepository = userRepo;
    }

    // --- LOGIN LOGIC ---

    /**
     * Authenticates a user with email and password.
     * Initializes the UserSession singleton upon success.
     */
    public ServiceResult login(LoginRequest request) {
        ServiceResult result = new ServiceResult();

        // 1. Input Validations
        if (request.email() == null || request.email().trim().isEmpty()) {
            result.addError("email", "Email address is required*");
        }
        if (request.password() == null || request.password().trim().isEmpty()) {
            result.addError("password", "Password is required*");
        }
        if (!result.isSuccess()) return result;

        // 2. Credential Verification
        User user = userRepository.findByEmail(request.email());
        if (user != null && user.password().equals(request.password())) {
            userSession.startSession(user);
            return result; // Success
        } else {
            result.setGlobalError("Invalid email or password.");
            return result;
        }
    }

    // --- REGISTRATION LOGIC ---
    
    /**
     * Step 1 of Registration: Validates input, generates an OTP, 
     * stores data temporarily, and sends an email.
     */
    public ServiceResult initiateRegistration(RegisterRequest request) {
        ServiceResult result = new ServiceResult();
        
        // 1. Comprehensive Input Validation (Regex + Uniqueness check)
        if (request.name() == null || request.name().trim().isEmpty()) {
            result.addError("name", "Name is required*");
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (request.email() == null || request.email().trim().isEmpty()) { 
            result.addError("email", "Email is required*"); 
        } else if (!Pattern.compile(emailRegex).matcher(request.email()).matches()) {
            result.addError("email", "Invalid email format*"); 
        } else if (userRepository.findByEmail(request.email()) != null) { 
            result.addError("email", "Email already in use*"); 
        }

        if (request.password() == null || request.password().trim().isEmpty()) {
            result.addError("password", "Password is required*");
        }
        
        if (!result.isSuccess()) return result;

        // 2. Generate 4-digit OTP & Cache Data in Memory
        String otp = String.format("%04d", new Random().nextInt(10000));
        otpStorage.put(request.email(), otp);
        
        // Store temp user data (not yet committed to DB)
        tempRegistrations.put(request.email(), new User(0, request.name(), request.email(), request.password(), UserRole.CUSTOMER));

        // 3. Trigger Email Notification via Observer
        Notification message = NotificationFactory.createOtp(request.email(), otp);
        notificationManager.notifyAll(message);
        
        return result;
    }

    /**
     * Step 2 of Registration: Verifies the OTP entered by the user.
     * If valid, commits the user to the database and clears temp storage.
     */
    public boolean verifyAndRegister(String email, String enteredOtp) {
        String validOtp = otpStorage.get(email);
        
        if (validOtp != null && validOtp.equals(enteredOtp)) {
            // OTP Match: Commit User to DB
            User tempUser = tempRegistrations.get(email);
            userRepository.addUser(tempUser.name(), tempUser.email(), tempUser.password());
            
            // Clean up memory to prevent leaks/replay
            otpStorage.remove(email);
            tempRegistrations.remove(email);
            
            // Send Welcome Email
            User newUser = userRepository.findByEmail(email);
            Notification message = NotificationFactory.createRegistration(newUser);
            notificationManager.notifyAll(message); 
            return true;
        }
        return false;
    }

    /**
     * Handles Google OAuth logic. Finds existing user or creates a new one 
     * automatically if they don't exist (JIT Provisioning).
     */
    public User loginWithGoogle(String googleEmail, String googleName) {
        User user = userRepository.findByEmail(googleEmail);
        
        if (user == null) {
            // New Google User: Auto-register
            userRepository.addUser(googleName, googleEmail, "GOOGLE_AUTH_USER");
            user = userRepository.findByEmail(googleEmail);
            
            Notification message = NotificationFactory.createRegistration(user);
            notificationManager.notifyAll(message);
        }
        // Start session for the Google User
        userSession.startSession(user);
        return user;
    }
}