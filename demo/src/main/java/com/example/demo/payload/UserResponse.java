package com.example.demo.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private List<UserDTO> users;
    private Integer pageNumber ;
    private Integer pageSize ;
    private Long totalElements ;
    private Long totalPages ;

    private boolean LastPage ;
}
