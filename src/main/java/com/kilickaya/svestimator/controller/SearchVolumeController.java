package com.kilickaya.svestimator.controller;

import com.kilickaya.svestimator.Util.NumericUtils;
import com.kilickaya.svestimator.dto.EstimationDTO;
import com.kilickaya.svestimator.service.ISearchVolumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchVolumeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchVolumeController.class);
    private static final Double INVALID_ESTIMATION = -1d;
    @Autowired
    ISearchVolumeService searchVolumeService;

    @GetMapping("/estimate")
    public ResponseEntity estimateSearchVolume(@RequestParam String keyword) {
        Double estimation = null;
        try {
            estimation = searchVolumeService.estimateSearchVolume(keyword);
        } catch (Exception e) {
            LOGGER.error("An error occurred", e);
            ResponseEntity.ok(new EstimationDTO(keyword, INVALID_ESTIMATION));
        }
        return ResponseEntity.ok(new EstimationDTO(keyword, NumericUtils.formatDouble(estimation)));
    }
}
