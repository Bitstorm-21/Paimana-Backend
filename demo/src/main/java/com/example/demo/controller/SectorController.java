package com.example.demo.controller;

import com.example.demo.config.AppConstants;
import com.example.demo.model.Sector;
import com.example.demo.payload.SectorDTO;
import com.example.demo.payload.SectorResponse;
import com.example.demo.repositories.SectorRepository;
import com.example.demo.security.services.SectorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SectorController {
    @Autowired
    SectorRepository sectorRepository;
    @Autowired
    SectorService sectorService;
    @GetMapping("/public/sector")
    public ResponseEntity<SectorResponse> getallSector(   @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                  @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                  @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_Sector_BY, required = false) String sortBy,
                                                  @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder)
    {
        SectorResponse sectorResponse = sectorService.getAllSectors(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(sectorResponse, HttpStatus.OK);
    }
@PostMapping("/admin/create/sector")
    public ResponseEntity<SectorDTO> createSector(@Valid @RequestBody SectorDTO sectorDTO) {
        SectorDTO sectorDTO1 = sectorService.createSector(sectorDTO) ;
        return new ResponseEntity<>(sectorDTO1, HttpStatus.OK);
}
@DeleteMapping("/admin/delete/{id}/sector")
    public ResponseEntity<SectorDTO> deleteSector(@PathVariable int id) {
        SectorDTO sectorDTO = sectorService.deleteSector(id);
        return new ResponseEntity<>(sectorDTO, HttpStatus.OK);
}
@PutMapping("/admin/update/{id}/sector")
    public ResponseEntity<SectorDTO> updateSector(@Valid @RequestBody SectorDTO sectorDTO, @PathVariable int id) {
        SectorDTO sectorDTO1 = sectorService.updateSector(id, sectorDTO.getSectorName()) ;
        return new ResponseEntity<>(sectorDTO1, HttpStatus.OK);
}
}
