package com.projet.mycose.controller;

import com.projet.mycose.service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EtudiantController.class)
public class EtudiantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EtudiantService etudiantService;


}
