package com.example.demo.payload;


import com.example.demo.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId ;
    private String username ;
    private String email ;
    private String password ;
    private Set<Role> roles=new HashSet<>();

}
