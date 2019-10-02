package com.kilickaya.svestimator.dto;

public class EstimationDTO {
    private final String keyword;
    private final Double score;

    public EstimationDTO(String keyword, Double score) {
        this.keyword = keyword;
        this.score = score;
    }

    public String getKeyword() {
        return keyword;
    }

    public Double getScore() {
        return score;
    }
}
