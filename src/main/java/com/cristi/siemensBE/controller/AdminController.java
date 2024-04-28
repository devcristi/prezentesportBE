package com.cristi.siemensBE.controller;
import com.cristi.siemensBE.model.Code;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final Map<String, LocalDateTime> generatedCodes = new ConcurrentHashMap<>(); // Mapa pentru a stoca codurile generate și momentul generării lor
    private static final long CODE_EXPIRATION_SECONDS = 30; // Durata de valabilitate a codului în secunde

    @PostMapping("/generateCode")
    public ResponseEntity<String> generateCode() {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        LocalDateTime generationTime = LocalDateTime.now();
        generatedCodes.put(code, generationTime); // Adaugă codul și timpul generării în mapă
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @PostMapping("/checkCode")
    public ResponseEntity<Boolean> checkCode(@RequestBody Code receivedCode) {
        try {
            LocalDateTime generationTime = generatedCodes.get(receivedCode.getCode());
            if (generationTime != null &&
                    ChronoUnit.SECONDS.between(generationTime, LocalDateTime.now()) <= CODE_EXPIRATION_SECONDS) {
                // Codul este valid și nu a expirat încă
                generatedCodes.remove(receivedCode.getCode()); // Șterge codul după ce a fost folosit
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
