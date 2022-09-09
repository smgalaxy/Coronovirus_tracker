package com.example.coronovirustracker.controller;

import com.example.coronovirustracker.model.LocationStats;
import com.example.coronovirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
     public String home(Model model){
        List<LocationStats> allStats= coronaVirusDataService.getAllStatsList();
        int totalReportedCases = allStats.stream().mapToInt(stats -> stats.getLatestTotalCase()).sum();
        int totalNewCases = allStats.stream().mapToInt(stats->stats.getCasesDiffFromPreviousDay()).sum();
        System.out.println("totalReportedCases = "+totalReportedCases);
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);
        return "home";
    }
}
