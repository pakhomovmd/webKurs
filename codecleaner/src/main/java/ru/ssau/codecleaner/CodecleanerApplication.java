package ru.ssau.codecleaner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import ru.ssau.codecleaner.entity.User;
import ru.ssau.codecleaner.entity.Role;
import ru.ssau.codecleaner.repository.UserRepository;

import java.time.LocalDateTime;

@SpringBootApplication
public class CodecleanerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodecleanerApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			if (userRepository.count() == 0) {
				User user = new User();
				user.setEmail("test@mail.com");
				user.setPassword("123");
				user.setFullName("Test User");
				user.setRole(Role.ADMIN);
				user.setCreatedAt(LocalDateTime.now());

				userRepository.save(user);
			}
		};
	}
}