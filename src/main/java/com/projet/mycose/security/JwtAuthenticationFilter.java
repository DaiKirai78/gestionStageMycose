package com.projet.mycose.security;

import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.exceptions.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final UtilisateurRepository utilisateurRepository;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UtilisateurRepository utilisateurRepository) {
        this.tokenProvider = tokenProvider;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Only process API endpoints
        if (!path.startsWith("/api/")) {
            //Filtre parce que presque tous les endpoints sont mal appelés!!!!!!!!!
            if (!path.startsWith("/contrat") && !path.startsWith("/entreprise") && !path.startsWith("/enseignant") && !path.startsWith("/gestionnaire") && !path.startsWith("/etudiant") && !path.startsWith("/utilisateur")) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Allow les endpoints publiques
        if (path.startsWith("/api/programme") || path.startsWith("/api/offres-stages/years") || path.startsWith("/api/offres-stages/sessions")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = getJWTFromRequest(request);
        try {
            tokenProvider.validateToken(token);
            String courriel = tokenProvider.getEmailFromJWT(token);
            Utilisateur user = utilisateurRepository.findUtilisateurByCourriel(courriel).orElseThrow(UserNotFoundException::new);

            CustomUserDetails userDetails = new CustomUserDetails(
                    user.getId(),
                    user.getCourriel(),
                    user.getAuthorities()
            );

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, user.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (UserNotFoundException e) {
            logger.error("User not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
        }
        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer "
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();

        // Exclude OPTIONS requests
        return "OPTIONS".equalsIgnoreCase(method);
    }
}
