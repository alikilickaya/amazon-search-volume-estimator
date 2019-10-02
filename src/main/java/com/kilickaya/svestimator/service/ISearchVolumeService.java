package com.kilickaya.svestimator.service;

import java.util.concurrent.ExecutionException;

public interface ISearchVolumeService {
    Double estimateSearchVolume(String keyword) throws ExecutionException, InterruptedException;
}
