package com.projet.mycose.controller;

import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.JWTAuthResponse;
import com.projet.mycose.service.dto.LoginDTO;
import com.projet.mycose.service.dto.UtilisateurDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> authentifierUtilisateur(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            String accessToken = utilisateurService.authentificationUtilisateur(loginDTO);
            final JWTAuthResponse authResponse= new JWTAuthResponse(accessToken);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JWTAuthResponse());
        }
    }

    @PostMapping("/me")
    public ResponseEntity<UtilisateurDTO> getMe(HttpServletRequest request) {
        try {
            return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(
                    utilisateurService.getMe(request.getHeader("Authorization"))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
