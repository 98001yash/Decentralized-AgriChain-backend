package com.company.Decentralized_AgriChain_backend.controller;


import com.company.Decentralized_AgriChain_backend.dtos.ProduceDto;
import com.company.Decentralized_AgriChain_backend.dtos.TransferProduceDto;
import com.company.Decentralized_AgriChain_backend.service.ProduceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produces")
@RequiredArgsConstructor
@Slf4j
public class ProduceController {

    private final ProduceService produceService;


    @PostMapping
    public ResponseEntity<ProduceDto> addProduce(@RequestBody ProduceDto produceDTO) {
        log.info("API request to add produce: {}", produceDTO);
        ProduceDto saved = produceService.addProduce(produceDTO);
        return ResponseEntity.ok(saved);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProduceDto> getProduceById(@PathVariable Long id) {
        log.info("API request to get produce by ID: {}", id);
        return ResponseEntity.ok(produceService.getProduceById(id));
    }


    @GetMapping("/owner/{actorId}")
    public ResponseEntity<List<ProduceDto>> getProduceByOwner(@PathVariable Long actorId) {
        log.info("API request to get produces for owner ID: {}", actorId);
        return ResponseEntity.ok(produceService.getProduceByOwner(actorId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ProduceDto> transferProduce(@RequestBody TransferProduceDto transferProduceDTO) {
        log.info("API request to transfer produce: {}", transferProduceDTO);
        ProduceDto updated = produceService.transferProduce(transferProduceDTO);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/all")
    public ResponseEntity<List<ProduceDto>> getAllProduces() {
        log.info("API request to get all produces");
        List<ProduceDto> produces = produceService.getAllProduces(); // implement in service
        return ResponseEntity.ok(produces);
    }

}