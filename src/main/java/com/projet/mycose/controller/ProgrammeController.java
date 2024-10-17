package com.projet.mycose.controller;

import com.projet.mycose.modele.Programme;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/programme")
public class ProgrammeController {

    @GetMapping
    public List<String> getProgrammes() {
        List<String> programmes = new ArrayList<>(Arrays.stream(Programme.values())
                .map(Programme::toString)
                .toList());
        programmes.remove("NOT_SPECIFIED");
        return programmes;
    }
}
