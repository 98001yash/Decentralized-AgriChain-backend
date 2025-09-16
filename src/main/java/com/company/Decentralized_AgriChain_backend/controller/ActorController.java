package com.company.Decentralized_AgriChain_backend.controller;


import com.company.Decentralized_AgriChain_backend.dtos.ActorDto;
import com.company.Decentralized_AgriChain_backend.service.ActorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
@Slf4j
public class ActorController {

    private final ActorService actorService;

    @PostMapping
    public ResponseEntity<ActorDto> createActor(@RequestBody ActorDto actorDto) {
        log.info("API request to create actor: {}", actorDto);
        ActorDto saved = actorService.createActor(actorDto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDto> getActorById(@PathVariable Long id) {
        log.info("API request to get actor by ID: {}", id);
        return ResponseEntity.ok(actorService.getActorById(id));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<ActorDto>> getActorsByRole(@PathVariable String role) {
        log.info("API request to get actors by role: {}", role);
        return ResponseEntity.ok(actorService.getActorsByRole(role));
    }

}
