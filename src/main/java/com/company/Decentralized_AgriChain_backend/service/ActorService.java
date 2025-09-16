package com.company.Decentralized_AgriChain_backend.service;

import com.company.Decentralized_AgriChain_backend.dtos.ActorDto;

import java.util.List;

public interface ActorService {

    ActorDto createActor(ActorDto actorDto);
    ActorDto getActorById(Long id);
    List<ActorDto> getActorsByRole(String role);
}
