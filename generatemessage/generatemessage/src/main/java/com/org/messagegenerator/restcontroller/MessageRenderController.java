package com.org.messagegenerator.restcontroller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageRenderController {

	@GetMapping("/generate")
	public ResponseEntity<String> generateMessage() {
		LocalDateTime ldt = LocalDateTime.now();
		int hour = ldt.getHour();
		String body = null;

		if (hour < 12) {
			body = "Good Morning";
		} else if (hour < 16) {
			body = "Good Afternoon";
		} else if (hour < 20) {
			body = "Good Evening";
		} else {
			body = "Good Night";
		}

		HttpStatus status = HttpStatus.OK;
		ResponseEntity<String> entity = new ResponseEntity<>(body, status);
		return entity;
	}
}
