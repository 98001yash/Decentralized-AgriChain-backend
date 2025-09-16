package com.company.Decentralized_AgriChain_backend.service.Impl;

import com.company.Decentralized_AgriChain_backend.dtos.ActorDto;
import com.company.Decentralized_AgriChain_backend.enitites.Actor;
import com.company.Decentralized_AgriChain_backend.enums.Role;
import com.company.Decentralized_AgriChain_backend.exception.ResourceNotFoundException;
import com.company.Decentralized_AgriChain_backend.repository.ActorRepository;
import com.company.Decentralized_AgriChain_backend.service.ActorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;
    private final ModelMapper modelMapper;


    @Override
    public ActorDto createActor(ActorDto actorDto) {
        log.info("Creating a new actor with name:{}",actorDto.getName());
        Actor actor = modelMapper.map(actorDto, Actor.class);
        actor.setCreatedAt(LocalDateTime.now());
        Actor saved = actorRepository.save(actor);

        log.debug("Actor saved with id: {}",saved.getId());
        return modelMapper.map(saved, ActorDto.class);
    }

    @Override
    public ActorDto getActorById(Long id) {
       log.info("Fetching actor with ID: {}",id);
       Actor actor = actorRepository.findById(id)
               .orElseThrow(()->new ResourceNotFoundException("Actor not found with ID: "+id));
       return modelMapper.map(actor, ActorDto.class);
    }

    @Override
    public List<ActorDto> getActorsByRole(String role) {
       log.info("Fetching all actors with role: {}",role);
       Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid role: " + role);
        }

        return actorRepository.findByRole(roleEnum)
                .stream()
                .map(actor -> modelMapper.map(actor, ActorDto.class))
                .collect(Collectors.toList());
    }
}
