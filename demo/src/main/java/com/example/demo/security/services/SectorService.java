package com.example.demo.security.services;

import com.example.demo.payload.SectorDTO;
import com.example.demo.payload.SectorResponse;

public interface SectorService {
    SectorResponse getAllSectors(int pageNumber, int pageSize, String sortBy, String sortOrder);
    SectorDTO createSector(SectorDTO sectorDTO);
    SectorDTO updateSector(int id,String name);
    SectorDTO deleteSector(int id);
}
