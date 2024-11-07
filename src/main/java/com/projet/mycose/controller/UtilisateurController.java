package com.projet.mycose.controller;

import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
            final JWTAuthResponse authResponse = new JWTAuthResponse(accessToken);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JWTAuthResponse());
        }
    }

    @PostMapping("/me")
    public ResponseEntity<UtilisateurDTO> getMe() {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    utilisateurService.getMe()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/register/check-for-conflict")
    public ResponseEntity<Object> CreationDeCompte_CheckForConflict(@Valid @RequestBody CourrielTelephoneDTO courrielTelephoneDTO) {
        if (utilisateurService.credentialsDejaPris(courrielTelephoneDTO.getCourriel(), courrielTelephoneDTO.getTelephone()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("L'utilisateur existe déjà ou les credentials sont invalides");
        else
            return ResponseEntity.status(HttpStatus.OK).body(courrielTelephoneDTO);
    }

    @GetMapping("/getPrenomNom")
    public ResponseEntity<String> getNomEtudiantSelonId(@RequestParam long id) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    utilisateurService.getUtilisateurPrenomNom(id));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
