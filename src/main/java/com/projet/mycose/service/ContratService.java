package com.projet.mycose.service;

import com.projet.mycose.repository.ContratRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;

}
