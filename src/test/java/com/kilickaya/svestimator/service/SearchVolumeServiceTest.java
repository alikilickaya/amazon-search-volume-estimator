package com.kilickaya.svestimator.service;

import com.kilickaya.svestimator.constant.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchVolumeServiceTest {
    private static final String KEYWORD = "nike";
    @InjectMocks
    SearchVolumeService searchVolumeService;
    @Mock
    AutocompleteService autocompleteService;

    @Test
    public void shouldReturnMinScore() throws ExecutionException, InterruptedException {
        when(autocompleteService.callApi(KEYWORD)).thenReturn(Collections.emptySet());

        Double estimation = searchVolumeService.estimateSearchVolume(KEYWORD);

        assertEquals(Constants.MIN_SCORE, estimation);
        verify(autocompleteService, times(2)).callApi(anyString());
    }

    @Test
    public void shouldReturnMaxScore() throws ExecutionException, InterruptedException {
        when(autocompleteService.callApi(KEYWORD.substring(0, 1))).thenReturn(Collections.singleton(KEYWORD));
        when(autocompleteService.callApi(KEYWORD)).thenReturn(Collections.singleton(KEYWORD));

        Double estimation = searchVolumeService.estimateSearchVolume(KEYWORD);

        assertEquals(Constants.MAX_SCORE, estimation);
        verify(autocompleteService, times(2)).callApi(anyString());
    }

    @Test
    public void shouldCallApiForEachChar() throws ExecutionException, InterruptedException {
        when(autocompleteService.callApi(KEYWORD.substring(0, 1))).thenReturn(Collections.emptySet());
        when(autocompleteService.callApi(KEYWORD)).thenReturn(Collections.singleton(KEYWORD));

        Double estimation = searchVolumeService.estimateSearchVolume(KEYWORD);

        assertTrue(estimation > 0);
        verify(autocompleteService, times(KEYWORD.length())).callApi(anyString());
    }
}
