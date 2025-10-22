package com.example.airline_platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AviationStack {  // ← Better class name


    private String apiKey="71c5fb8d50e46afd25de9b1894c430f4";


    private final String baseUrl = "http://api.aviationstack.com/v1/";

    public List<Map<String, Object>> searchFlights(String departureIata, String arrivalIata, String flightDate) {  // ← Changed Objects to Object

        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "flights?access_key=" + apiKey;



        System.out.println(apiKey);

        if (departureIata != null && !departureIata.isEmpty()) {
            url += "&dep_iata=" + departureIata;
        }
        if (arrivalIata != null && !arrivalIata.isEmpty()) {
            url += "&arr_iata=" + arrivalIata;
        }
        if (flightDate != null && !flightDate.isEmpty()) {
            url += "&flight_date=" + flightDate;
        }
        System.out.println("AviationStack API URL: " + url);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("data")) {
                return (List<Map<String, Object>>) response.get("data");
            } else {
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("AviationStack API error: " + e.getMessage());
            return List.of();
        }
    }
}