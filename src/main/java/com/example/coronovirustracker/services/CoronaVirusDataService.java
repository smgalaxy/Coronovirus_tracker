package com.example.coronovirustracker.services;

import com.example.coronovirustracker.model.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service

public class CoronaVirusDataService {
    private static final String VIRUS_DATA_URL =
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStatsList = new ArrayList<>();

    public List<LocationStats> getAllStatsList() {
        return allStatsList;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    private void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStatList = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponse.body());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            locationStats.setState(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));

            int latestCases = Integer.parseInt(record.get(record.size()-1));
            int previousDayCases = Integer.parseInt(record.get(record.size()-2));
            locationStats.setLatestTotalCase(latestCases);
            locationStats.setCasesDiffFromPreviousDay(latestCases - previousDayCases);
            //System.out.println(locationStats);
            newStatList.add(locationStats);
        }
        this.allStatsList = newStatList;

    }

}
