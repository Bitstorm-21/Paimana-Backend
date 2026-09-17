package com.example.demo.security.services;

import com.example.demo.exceptions.APIException;
import com.example.demo.model.Sector;
import com.example.demo.payload.SectorDTO;
import com.example.demo.payload.SectorResponse;
import com.example.demo.repositories.SectorRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class SectorServiceImpl implements SectorService {
    private static final String DEFAULT_SORT_FIELD = "sectorId";
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "sector", DEFAULT_SORT_FIELD,
            "id", DEFAULT_SORT_FIELD,
            "sectorId", DEFAULT_SORT_FIELD,
            "sector_id", DEFAULT_SORT_FIELD,
            "sectorname", "sectorName",
            "sectorName", "sectorName",
            "SectorName", "sectorName",
            "name", "sectorName"
    );

    @Autowired
    private SectorRepository sectorRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SectorResponse getAllSectors(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortOrder) {

        String sortField = resolveSortField(sortBy);
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageDetails =
                PageRequest.of(pageNumber, pageSize, sort);

        Page<Sector> sectorPage =
                sectorRepository.findAll(pageDetails);

        List<Sector> sectors = sectorPage.getContent();

        if (sectors.isEmpty()) {
            throw new APIException("No sector created till now.");
        }

        List<SectorDTO> sectorDTOs = sectors.stream()
                .map(sector -> modelMapper.map(sector, SectorDTO.class))
                .toList();

        SectorResponse sectorResponse = new SectorResponse();

        sectorResponse.setContent(sectorDTOs);
        sectorResponse.setPageNumber(sectorPage.getNumber());
        sectorResponse.setPageSize(sectorPage.getSize());
        sectorResponse.setTotalElements(sectorPage.getTotalElements());
        sectorResponse.setTotalpages(sectorPage.getTotalPages());
        sectorResponse.setLastPage(sectorPage.isLast());

        return sectorResponse;
    }

    private String resolveSortField(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return DEFAULT_SORT_FIELD;
        }

        String normalizedSortBy = sortBy.trim();
        return SORT_FIELDS.getOrDefault(
                normalizedSortBy,
                SORT_FIELDS.getOrDefault(normalizedSortBy.toLowerCase(Locale.ROOT), DEFAULT_SORT_FIELD)
        );
    }

    @Override
    public SectorDTO createSector(SectorDTO sectorDTO) {
        String sectorName = sectorDTO.getSectorName().trim();
        sectorDTO.setSectorName(sectorName);

        Sector sector = modelMapper.map(sectorDTO, Sector.class);
        Sector sectorFromDb = sectorRepository.findBySectorName(sectorName);

        if (sectorFromDb != null) {
            throw new APIException("Sector already present !");
        }

        return modelMapper.map(sectorRepository.save(sector), SectorDTO.class);
    }
    @Override
    public SectorDTO updateSector(int id, String name) {
        String sectorName = name.trim();
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new APIException(" Sector not found! !"));

        Sector sectorFromDb = sectorRepository.findBySectorName(sectorName);
        if (sectorFromDb != null && sectorFromDb.getSectorId() != id) {
            throw new APIException("Sector already present !");
        }

        sector.setSectorName(sectorName);
        sector = sectorRepository.save(sector);
        return modelMapper.map(sector, SectorDTO.class);
    }
    @Override
    public SectorDTO deleteSector(int id) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new APIException(" Sector not found! !"));

        sectorRepository.delete(sector);
        return modelMapper.map(sector, SectorDTO.class);
    }


}
