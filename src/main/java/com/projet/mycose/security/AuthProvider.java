package com.projet.mycose.security;

import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.security.exception.AuthenticationException;
import com.projet.mycose.security.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider{
	private final PasswordEncoder passwordEncoder;
	private final UtilisateurRepository utilisateurRepository;

	@Override
	public Authentication authenticate(Authentication authentication) {
		Utilisateur user = loadUserByEmail(authentication.getPrincipal().toString());
		validateAuthentication(authentication, user);
		return new UsernamePasswordAuthenticationToken(
			user.getCourriel(),
			user.getMotDePasse(),
			user.getAuthorities()
		);
	}

	@Override
	public boolean supports(Class<?> authentication){
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private Utilisateur loadUserByEmail(String courriel) throws UsernameNotFoundException{
		return utilisateurRepository.findUtilisateurByCourriel(courriel)
			.orElseThrow(UserNotFoundException::new);
	}

	private void validateAuthentication(Authentication authentication, Utilisateur user){
		if(!passwordEncoder.matches(authentication.getCredentials().toString(), user.getMotDePasse()))
			throw new AuthenticationException(HttpStatus.FORBIDDEN, "Incorrect username or password");
	}
}
