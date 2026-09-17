package com.example.demo.controller;

import com.example.demo.config.AppConstants;
import com.example.demo.payload.MonthlyProgressDTO;
import com.example.demo.payload.MonthlyProgressRequest;
import com.example.demo.payload.MonthlyProgressResponse;
import com.example.demo.security.services.MonthlyProgressService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MonthlyProgressController {

    @Autowired
    private MonthlyProgressService monthlyProgressService;


    // =====================================================
    // CREATE MONTHLY PROGRESS
    // =====================================================

    @PostMapping("/admin/create/monthly-progress/project/{projectId}")
    public ResponseEntity<MonthlyProgressDTO> createMonthlyProgress(
            @PathVariable Long projectId,
            @Valid @RequestBody MonthlyProgressRequest monthlyProgressRequest) {

        MonthlyProgressDTO createdMonthlyProgress =
                monthlyProgressService.createMonthlyProgress(
                        projectId,
                        monthlyProgressRequest
                );

        return new ResponseEntity<>(
                createdMonthlyProgress,
                HttpStatus.CREATED
        );
    }


    // =====================================================
    // GET ALL MONTHLY PROGRESS
    // =====================================================

    @GetMapping("/public/monthly-progress")
    public ResponseEntity<MonthlyProgressResponse> getAllMonthlyProgress(

            @RequestParam(
                    name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER,
                    required = false
            )
            Integer pageNumber,

            @RequestParam(
                    name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE,
                    required = false
            )
            Integer pageSize,

            @RequestParam(
                    name = "sortBy",
                    defaultValue = AppConstants.SORT_MONTHLY_PROGRESS_BY,
                    required = false
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR,
                    required = false
            )
            String sortOrder) {


        MonthlyProgressResponse monthlyProgressResponse =
                monthlyProgressService.getAllMonthlyProgress(
                        pageNumber,
                        pageSize,
                        sortBy,
                        sortOrder
                );


        return new ResponseEntity<>(
                monthlyProgressResponse,
                HttpStatus.OK
        );
    }


    // =====================================================
    // GET MONTHLY PROGRESS BY PROJECT
    // =====================================================

    @GetMapping("/public/monthly-progress/project/{projectId}")
    public ResponseEntity<MonthlyProgressResponse>
    getMonthlyProgressByProject(

            @PathVariable Long projectId,

            @RequestParam(
                    name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER,
                    required = false
            )
            Integer pageNumber,

            @RequestParam(
                    name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE,
                    required = false
            )
            Integer pageSize,

            @RequestParam(
                    name = "sortBy",
                    defaultValue = AppConstants.SORT_MONTHLY_PROGRESS_BY,
                    required = false
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR,
                    required = false
            )
            String sortOrder) {


        MonthlyProgressResponse monthlyProgressResponse =
                monthlyProgressService.getMonthlyProgressByProject(
                        projectId,
                        pageNumber,
                        pageSize,
                        sortBy,
                        sortOrder
                );


        return new ResponseEntity<>(
                monthlyProgressResponse,
                HttpStatus.OK
        );
    }
}
//    @PutMapping("/admin/update/project/{projectId}/monthly-progress/{monthName}")
//    public ResponseEntity<MonthlyProgressDTO> updateMonthlyProgress(@PathVariable Long projectId,
//                                                                    @PathVariable String monthName,
//                                                                    @Valid @RequestBody MonthlyProgressDTO monthlyProgressDTO) {
//        MonthlyProgressDTO updatedMonthlyProgress = monthlyProgressService.updateMonthlyProgress(projectId, monthName, monthlyProgressDTO);
//        return new ResponseEntity<>(updatedMonthlyProgress, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/admin/delete/project/{projectId}/monthly-progress/{monthName}")
//    public ResponseEntity<MonthlyProgressDTO> deleteMonthlyProgress(@PathVariable Long projectId,
//                                                                    @PathVariable String monthName) {
//        MonthlyProgressDTO deletedMonthlyProgress = monthlyProgressService.deleteMonthlyProgress(projectId, monthName);
//        return new ResponseEntity<>(deletedMonthlyProgress, HttpStatus.OK);
//    }


