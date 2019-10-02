package com.kilickaya.svestimator.service;

import com.kilickaya.svestimator.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Service
public class SearchVolumeService implements ISearchVolumeService {
    private final static Double EXPONENTIAL_FACTOR = 1.25d;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    IAutocompleteService autocompleteService;

    /**
     * @param keyword
     * @return estimation score of the keyword
     */
    @Override
    public Double estimateSearchVolume(String keyword) throws ExecutionException, InterruptedException {
        if (StringUtils.isEmpty(keyword)) {
            return Constants.MIN_SCORE;
        }
        Double unitScore = calculateScoreOfUnit(keyword);

        Future<Double> easiestScoreFuture = callForEasiestScore(keyword, unitScore);
        Future<Double> hardestScoreFuture = callForHardestScore(keyword, unitScore);

        Double easiestScore = easiestScoreFuture.get();
        if (easiestScore == 0) {
            return Constants.MIN_SCORE;
        }

        Double hardestScore = hardestScoreFuture.get();
        if (hardestScore > 0) {
            return Constants.MAX_SCORE;
        }

        return easiestScore + hardestScore + getTotalScoreForOtherCases(keyword, unitScore);
    }

    /**
     * Calculates total scores of the rest possibilities. As easiest score (keyword itself) and
     * hardest score (first character) has been already calculated.
     * @param keyword
     * @param unitScore
     * @return
     */
    private Double getTotalScoreForOtherCases(String keyword, Double unitScore) {
        return IntStream.range(1, keyword.length() - 1)
                    .boxed()
                    .parallel()
                    .map(i -> keyword.substring(0, keyword.length() - i))
                    .mapToDouble(query -> getScoreOfQuery(query, keyword, unitScore))
                    .sum();
    }

    /**
     * calling for the hardest score which is the score of first character of the keyword
     */
    private Future<Double> callForHardestScore(String keyword, Double unitScore) {
        return executor.submit(() -> getScoreOfQuery(keyword.substring(0, 1), keyword, unitScore));
    }

    /**
     * calling for the easiest score which is the score of keyword itself
     */
    private Future<Double> callForEasiestScore(String keyword, Double unitScore) {
        return executor.submit(() -> getScoreOfQuery(keyword, keyword, unitScore));
    }

    /**
     * @return the score of the query for the keyword.
     */
    private Double getScoreOfQuery(String query, String keyword, Double unitScore) {
        Set<String> resultSet = autocompleteService.callApi(query);
        if (resultSet.contains(keyword)) {
            int powerLevel = keyword.length() - query.length();
            return getScoreForLevel(powerLevel, unitScore);
        }
        return 0d;
    }

    /**
     * @return score of a unit.
     * <p>
     * For example: keyword "nike" has 15 units.
     * so the score of a unit is maxScore(100) / totalNumberOfUnits(15) = 6.66
     */
    private Double calculateScoreOfUnit(String keyword) {
        Double maxScore = 100d;
        return maxScore / calculateTotalUnits(keyword);
    }

    /**
     * @return total number of units.
     * <p>
     * One unit score is easiest score that a keyword can get which is when the whole keyword is sent to the
     * Amazon's api.
     * <p>
     * For example: keyword is "nike"
     * query for;
     * "nike" is 2^0 = 1 unit
     * "nik" is 2^1 = 2 units
     * "ni" is 2^2 = 4 units
     * "n" is 2^3 = 8 units
     * <p>
     * so total number of units is 8 + 4 + 2 + 1 = 15 units.
     */
    private Double calculateTotalUnits(String keyword) {
        return IntStream.range(0, keyword.length()).mapToDouble(i -> Math.pow(EXPONENTIAL_FACTOR, i)).sum();
    }

    /**
     * @return the score of the level.
     * The score of the level is calculated as EXPONENTIAL_FACTOR^level * unitScore
     * for example: keyword "nike"
     * query for;
     * "nike" is level 0
     * "nik" is level 1
     * "ni" is level 2
     * "n" is level 3
     */
    private Double getScoreForLevel(int level, Double unitScore) {
        return Math.pow(EXPONENTIAL_FACTOR, level) * unitScore;
    }
}
