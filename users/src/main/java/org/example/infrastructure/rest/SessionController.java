package org.example.infrastructure.rest;

import org.example.model.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SessionController {
    private final SessionRepository sessionRepository;

    @Autowired
    public SessionController(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> create(@PathVariable("userId") String userId) {
        String sessionSecret = sessionRepository.saveSession(userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sessionSecret);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> checkSession(@PathVariable("userId") String userId, @RequestBody String sessionSecret) {
        if (sessionRepository.sessionSecretMatches(userId, sessionSecret)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
    }

}
