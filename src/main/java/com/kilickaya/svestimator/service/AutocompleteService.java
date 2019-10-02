package com.kilickaya.svestimator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Service
public class AutocompleteService implements IAutocompleteService {
    private  static final Logger LOGGER = LoggerFactory.getLogger(AutocompleteService.class);

    @Value("${amazon.us.autocomplete.api}")
    private String AMAZON_API_URL;

    @Override
    public Set<String> callApi(String query) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(AMAZON_API_URL + query, String.class);
        return getResultSet(response);
    }

    private Set<String> getResultSet(ResponseEntity<String> response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            ObjectReader reader = mapper.readerFor(new TypeReference<Set<String>>() {});
            return reader.readValue(root.get(1));
        } catch (IOException e) {
            LOGGER.error("Error parsing auto complete api result to set");
        }
        return Collections.emptySet();
    }
}
