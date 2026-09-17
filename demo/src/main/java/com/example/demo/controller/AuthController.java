package com.example.demo.controller;


import com.example.demo.payload.AuthenticationResult;
import com.example.demo.security.request.LoginRequest;
import com.example.demo.security.request.SignupRequest;
import com.example.demo.security.response.MessageResponse;
import com.example.demo.security.response.UserInfoResponse;
import com.example.demo.security.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import com.example.demo.security.services.UserDetailsImpl;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("auth/signin")
    public ResponseEntity<UserInfoResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthenticationResult result = authService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.getResponseCookie().toString())
                .body(result.getUserInfoResponse());
    }

    @PostMapping("auth/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse response = authService.register(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("auth/username")
    public String currentUserName(Authentication authentication) {
        if (authentication != null) {
            return authentication.getName();
        }
        return "";
    }

    @GetMapping("auth/user")
    public ResponseEntity<UserInfoResponse> getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles,
                userDetails.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("auth/signout")
    public ResponseEntity<MessageResponse> signoutUser() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authService.logout().toString())
                .body(new MessageResponse("You've been signed out!"));
    }
    @DeleteMapping("/admin/delete/{userId}/user")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long userId) {
     String name = authService.deleteUser(userId) ;
        return ResponseEntity.ok()
                .body(new MessageResponse(name + "is  deleted!"));
    }
    }

