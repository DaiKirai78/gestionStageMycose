package com.projet.mycose.controller;


import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.service.ContratService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("contrat")
public class ContratController {
    private final ContratService contratService;

//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> upload(@RequestParam MultipartFile contratPDF) {
//        try {
//
//        }
//    }
}
