package com.serviceWatch.service;

import org.springframework.stereotype.Service;
import com.serviceWatch.enums.Role;
import com.serviceWatch.exception.UserNotFoundException;

import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.UserRepository;
import com.serviceWatch.dto.UserRequestDTO;
import com.serviceWatch.dto.UserResponseDTO;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

this.userRepository = userRepository;
this.passwordEncoder = passwordEncoder;
}

	public UserResponseDTO createUser(UserRequestDTO request) {

	    User user = new User();

	    user.setName(request.getName());
	    user.setEmail(request.getEmail());
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    user.setRole(request.getRole());

	    User savedUser = userRepository.save(user);

	    return new UserResponseDTO(
	            savedUser.getId(),
	            savedUser.getName(),
	            savedUser.getEmail(),
	            savedUser.getRole().name(),
	            savedUser.getCreatedAt()
	    );
	}
    public java.util.List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getCreatedAt()
                ))
                .toList();
    }
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + id)
                );

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + id)
                );

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        existingUser.setRole(request.getRole());

        User savedUser = userRepository.save(existingUser);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getCreatedAt()
        );
    }
    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + id)
                );

        userRepository.delete(existingUser);
    }
}
