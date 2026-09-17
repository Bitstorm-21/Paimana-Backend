package com.example.demo.security.services;

import com.example.demo.payload.MonthlyProgressDTO;
import com.example.demo.payload.MonthlyProgressRequest;
import com.example.demo.payload.MonthlyProgressResponse;

public interface MonthlyProgressService {

    MonthlyProgressDTO createMonthlyProgress(
            Long projectId,
            MonthlyProgressRequest dto
    );


    MonthlyProgressResponse getAllMonthlyProgress(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    );


    MonthlyProgressResponse getMonthlyProgressByProject(
            Long projectId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    );
}