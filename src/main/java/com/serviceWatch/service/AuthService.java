package com.serviceWatch.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.serviceWatch.dto.LoginResponseDTO;
import com.serviceWatch.security.JwtService;
import org.springframework.stereotype.Service;

import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.UserRepository;
import com.serviceWatch.dto.LoginRequestDTO;
import com.serviceWatch.dto.LoginResponseDTO;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public LoginResponseDTO login(LoginRequestDTO request) {

	    User user = userRepository.findByEmail(request.getEmail())
	        .orElseThrow(() ->
	            new RuntimeException("Invalid email or password")
	        );

	    if (!passwordEncoder.matches(
	            request.getPassword(),
	            user.getPassword())) {

	        throw new RuntimeException("Invalid email or password");
	    }

	    String token = jwtService.generateToken(user);

	    return new LoginResponseDTO(
	        user.getId(),
	        user.getName(),
	        user.getEmail(),
	        user.getRole().name(),
	        token
	    );
	}
}