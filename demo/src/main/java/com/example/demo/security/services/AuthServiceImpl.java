package com.example.demo.security.services;

import com.example.demo.exceptions.APIException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.AppRole;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.payload.AuthenticationResult;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.request.LoginRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.security.request.LoginRequest;
import com.example.demo.security.request.SignupRequest;
import com.example.demo.security.response.MessageResponse;
import com.example.demo.security.response.UserInfoResponse;
import com.example.demo.security.services.UserDetailsImpl;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.demo.payload.UserResponse;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResult login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException("Invalid username or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenFromUsername(userDetails);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(jwt);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles,
                jwt,
                jwtCookie.toString(),
                userDetails.getEmail()
        );

        return new AuthenticationResult(response, jwtCookie);
    }

    @Override
    public MessageResponse register(SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            throw new APIException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new APIException("Email is already in use!");
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        Set<Role> roles = resolveRoles(signUpRequest.getRole());
        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
@Override
public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new APIException("User not found!"));
userRepository.delete(user);
return user.getUserName();
}
    @Override
    public ResponseCookie logout() {
        return jwtUtils.getCleanJwtCookie();
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        Set<Role> roles = new HashSet<>();

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            roles.add(getRole(AppRole.ROLE_USER));
            return roles;
        }

        for (String requestedRole : requestedRoles) {
            if (requestedRole == null) {
                continue;
            }

            switch (requestedRole.toLowerCase(Locale.ROOT)) {
                case "admin" -> roles.add(getRole(AppRole.ROLE_ADMIN));

                default -> roles.add(getRole(AppRole.ROLE_USER));
            }
        }

        if (roles.isEmpty()) {
            roles.add(getRole(AppRole.ROLE_USER));
        }

        return roles;
    }

    private Role getRole(AppRole roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new APIException("Error: Role is not found."));
    }

}

