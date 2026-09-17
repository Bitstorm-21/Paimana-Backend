package com.example.demo.security.response;

import java.util.List;

public class UserInfoResponse {
    private Long id;
    private String jwtToken;
    private String jwtCookie;
    private String username;
    private List<String> roles;
    private String email;

    public UserInfoResponse(Long id, String username, List<String> roles, String jwtToken, String jwtCookie, String email) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
        this.jwtCookie = jwtCookie;
        this.email = email;
    }

    public UserInfoResponse(Long id, String username, List<String> roles  ,String email) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtCookie() {
        return jwtCookie;
    }

    public void setJwtCookie(String jwtCookie) {
        this.jwtCookie = jwtCookie;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
