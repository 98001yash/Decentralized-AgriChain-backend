package com.company.Decentralized_AgriChain_backend.service.Impl;


import com.company.Decentralized_AgriChain_backend.dtos.ProduceDto;
import com.company.Decentralized_AgriChain_backend.dtos.TransferProduceDto;
import com.company.Decentralized_AgriChain_backend.enitites.Actor;
import com.company.Decentralized_AgriChain_backend.enitites.Produce;
import com.company.Decentralized_AgriChain_backend.enums.Status;
import com.company.Decentralized_AgriChain_backend.exception.ResourceNotFoundException;
import com.company.Decentralized_AgriChain_backend.repository.ActorRepository;
import com.company.Decentralized_AgriChain_backend.repository.ProduceRepository;
import com.company.Decentralized_AgriChain_backend.service.ProduceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProduceServiceImpl implements ProduceService {

    private final ProduceRepository produceRepository;
    private final ActorRepository actorRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProduceDto addProduce(ProduceDto produceDto) {
        log.info("Adding new produce: {}",produceDto.getName());
        Actor owner = actorRepository.findById(produceDto.getCurrentOwnerId())
                .orElseThrow(()->new ResourceNotFoundException("Owner not found with id: "+produceDto.getCurrentOwnerId()));

        Produce produce = modelMapper.map(produceDto, Produce.class);
        produce.setCurrentOwner(owner);
        produce.setStatus(Status.FARMER);
        produce.setAddedAt(LocalDateTime.now());

        Produce saved = produceRepository.save(produce);
        log.debug("Produce saved with ID: {}",saved.getId());
        return modelMapper.map(saved, ProduceDto.class);
    }

    @Override
    public ProduceDto getProduceById(Long id) {
       log.info("Fetching produce with id: {}",id);
       Produce produce = produceRepository.findById(id)
               .orElseThrow(()->new ResourceNotFoundException("Product not found with ID:"+id));
       return modelMapper.map(produce, ProduceDto.class);
    }

    @Override
    public List<ProduceDto> getProduceByOwner(Long actorId) {
        log.info("Fetching produce for owner {}",actorId);
        Actor owner = actorRepository.findById(actorId)
                .orElseThrow(()->new ResourceNotFoundException("Owner not found with id: "+actorId));

        return produceRepository.findByCurrentOwner(owner)
                .stream()
                .map(produce->modelMapper.map(produce, ProduceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProduceDto transferProduce(TransferProduceDto transferProduceDto) {
        log.info("Transferring produce ID {} from actor ID {} to actor ID {}",
                transferProduceDto.getProduceId(), transferProduceDto.getFromActorId(), transferProduceDto.getToActorId());


        Produce produce = produceRepository.findById(transferProduceDto.getProduceId())
                .orElseThrow(()->new ResourceNotFoundException("Produce not found with ID: "+transferProduceDto.getProduceId()));

        Actor fromActor = actorRepository.findById(transferProduceDto.getFromActorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with ID: " + transferProduceDto.getFromActorId()));


        Actor toActor = actorRepository.findById(transferProduceDto.getToActorId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with ID: " + transferProduceDto.getToActorId()));

        if(!produce.getCurrentOwner().getId().equals(fromActor.getId())){
            throw new ResourceNotFoundException("Produce si not owned by actor ID: "+fromActor.getId());
        }

        produce.setCurrentOwner(toActor);

        //update Status based on Actor's role
        switch(toActor.getRole()){
            case DISTRIBUTOR -> produce.setStatus(Status.DISTRIBUTOR);
            case RETAILER -> produce.setStatus(Status.RETAILER);
            default -> produce.setStatus(Status.SOLD);
        }

        Produce saved = produceRepository.save(produce);
        log.debug("Produce transferred: Nww owner to: {}",saved.getCurrentOwner().getId());
        return modelMapper.map(saved, ProduceDto.class);
    }
}
