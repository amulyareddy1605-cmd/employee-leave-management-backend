package com.example.employee_leave_management.controller;

import com.example.employee_leave_management.dto.RegisterRequest;
import com.example.employee_leave_management.entity.Employee;
import com.example.employee_leave_management.entity.User;
import com.example.employee_leave_management.repository.EmployeeRepository;
import com.example.employee_leave_management.repository.UserRepository;
import com.example.employee_leave_management.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            // ---------- Validate name ----------

            if (request.getName() == null ||
                    request.getName().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Name is required");
            }

            // ---------- Validate email ----------

            if (request.getEmail() == null ||
                    request.getEmail().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Email is required");
            }

            // ---------- Validate password ----------

            if (request.getPassword() == null ||
                    request.getPassword().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Password is required");
            }

            // ---------- Validate role ----------

            if (request.getRole() == null ||
                    request.getRole().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Role is required");
            }

            String role =
                    request.getRole()
                            .trim()
                            .toUpperCase();

            if (!role.equals("EMPLOYEE") &&
                    !role.equals("MANAGER")) {

                return ResponseEntity
                        .badRequest()
                        .body("Invalid role. Use EMPLOYEE or MANAGER");
            }

            String email =
                    request.getEmail()
                            .trim()
                            .toLowerCase();

            // ---------- Check duplicate user ----------

            if (userRepository
                    .findByEmail(email)
                    .isPresent()) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Email already registered");
            }

            // ---------- Check duplicate employee ----------

            if (employeeRepository
                    .findByEmail(email)
                    .isPresent()) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Employee with this email already exists");
            }

            // =================================================
            // CREATE USER
            // =================================================

            User user = new User();

            user.setName(
                    request.getName().trim()
            );

            user.setEmail(email);

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );

            user.setRole(role);

            User savedUser =
                    userRepository.save(user);

            // =================================================
            // CREATE EMPLOYEE
            // =================================================

            Employee employee = new Employee();

            employee.setName(
                    request.getName().trim()
            );

            employee.setEmail(email);

            employee.setDepartment(
                    request.getDepartment()
            );

            employee.setDesignation(
                    request.getDesignation()
            );

            employee.setRole(role);

            Employee savedEmployee =
                    employeeRepository.save(employee);

            // =================================================
            // RESPONSE
            // =================================================

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Account created successfully"
            );

            response.put(
                    "userId",
                    savedUser.getId()
            );

            response.put(
                    "employeeId",
                    savedEmployee.getId()
            );

            response.put(
                    "name",
                    savedUser.getName()
            );

            response.put(
                    "email",
                    savedUser.getEmail()
            );

            response.put(
                    "role",
                    savedUser.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Registration failed: "
                                    + e.getMessage()
                    );
        }
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            // ---------- Validate email ----------

            if (request.getEmail() == null ||
                    request.getEmail().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Email, password and role are required"
                        );
            }

            // ---------- Validate password ----------

            if (request.getPassword() == null ||
                    request.getPassword().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Email, password and role are required"
                        );
            }

            // ---------- Validate role ----------

            if (request.getRole() == null ||
                    request.getRole().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Email, password and role are required"
                        );
            }

            String email =
                    request.getEmail()
                            .trim()
                            .toLowerCase();

            String selectedRole =
                    request.getRole()
                            .trim()
                            .toUpperCase();

            // ---------- Validate selected role ----------

            if (!selectedRole.equals("EMPLOYEE") &&
                    !selectedRole.equals("MANAGER")) {

                return ResponseEntity
                        .badRequest()
                        .body("Invalid role");
            }

            // =================================================
            // FIND USER
            // =================================================

            Optional<User> userOptional =
                    userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            User user =
                    userOptional.get();

            // =================================================
            // CHECK PASSWORD
            // =================================================

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            // =================================================
            // CHECK ROLE
            // =================================================

            if (user.getRole() == null ||
                    !user.getRole()
                            .equalsIgnoreCase(selectedRole)) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(
                                "Selected role does not match this account"
                        );
            }

            // =================================================
            // FIND EMPLOYEE
            // =================================================

            Optional<Employee> employeeOptional =
                    employeeRepository.findByEmail(email);

            if (employeeOptional.isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                "Employee record not found for this account"
                        );
            }

            Employee employee =
                    employeeOptional.get();

            // =================================================
            // GENERATE JWT
            // =================================================

            String token =
                    jwtService.generateToken(
                            user.getEmail(),
                            user.getRole(),
                            employee.getId()
                    );

            // =================================================
            // RESPONSE
            // =================================================

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Login successful"
            );

            response.put(
                    "token",
                    token
            );

            response.put(
                    "userId",
                    user.getId()
            );

            response.put(
                    "employeeId",
                    employee.getId()
            );

            response.put(
                    "name",
                    user.getName()
            );

            response.put(
                    "email",
                    user.getEmail()
            );

            response.put(
                    "role",
                    user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Login failed: "
                                    + e.getMessage()
                    );
        }
    }


    // =========================================================
    // LOGIN REQUEST CLASS
    // =========================================================

    public static class LoginRequest {

        private String email;
        private String password;
        private String role;

        public LoginRequest() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}