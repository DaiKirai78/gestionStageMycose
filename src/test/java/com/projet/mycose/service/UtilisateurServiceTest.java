package com.projet.mycose.service;

import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;

public class UtilisateurServiceTest {

    @Test
    public void testAuthentificationEtudiant_Succes() {
        // Arrange
        UtilisateurRepository utilisateurRepository = Mockito.mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = Mockito.mock(EtudiantRepository.class);
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManager, jwtTokenProvider);


    }

}
