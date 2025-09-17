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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;
    private final ModelMapper modelMapper;

    private final Web3j web3j;
    private final Credentials credentials;
    private final String contractAddress;

    public ActorServiceImpl(ActorRepository actorRepository,
                            ModelMapper modelMapper,
                            @Value("${web3.rpcUrl}") String rpcUrl,
                            @Value("${web3.privateKey}") String privateKey,
                            @Value("${web3.contractAddress}") String contractAddress) {
        this.actorRepository = actorRepository;
        this.modelMapper = modelMapper;
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.credentials = Credentials.create(privateKey);
        this.contractAddress = contractAddress;
    }

    @Override
    public ActorDto createActor(ActorDto actorDto) {
        log.info("Creating a new actor with name: {}", actorDto.getName());


        Actor actor = modelMapper.map(actorDto, Actor.class);
        actor.setCreatedAt(LocalDateTime.now());
        Actor saved = actorRepository.save(actor);
        log.debug("Actor saved locally with id: {}", saved.getId());


        try {

            String functionName = switch (actorDto.getRole()) {
                case FARMER -> "addFarmer";
                case DISTRIBUTOR -> "addDistributor";
                case RETAILER -> "addRetailer";
            };

            // Build the function object
            org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                    functionName,
                    List.of(new org.web3j.abi.datatypes.Utf8String(actorDto.getName())),
                    List.of() // no outputs
            );

            // Encode the function call
            String encodedFunction = org.web3j.abi.FunctionEncoder.encode(function);

            // Create a transaction manager with credentials
            RawTransactionManager txManager = new RawTransactionManager(web3j, credentials);

            // Send the transaction
            EthSendTransaction txResponse = txManager.sendTransaction(
                    DefaultGasProvider.GAS_PRICE,
                    DefaultGasProvider.GAS_LIMIT,
                    contractAddress,
                    encodedFunction,
                    BigInteger.ZERO
            );

            // Check for errors
            if (txResponse.hasError()) {
                log.error("Blockchain transaction failed: {}", txResponse.getError().getMessage());
                throw new RuntimeException("Blockchain transaction failed: " + txResponse.getError().getMessage());
            }

            log.info("Actor added on blockchain, tx hash: {}", txResponse.getTransactionHash());

        } catch (Exception e) {
            log.error("Error while adding actor to blockchain", e);
            throw new RuntimeException("Blockchain integration failed", e);
        }

        return modelMapper.map(saved, ActorDto.class);
    }


    @Override
    public ActorDto getActorById(Long id) {
        log.info("Fetching actor with ID: {}", id);
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with ID: " + id));
        return modelMapper.map(actor, ActorDto.class);
    }

    @Override
    public List<ActorDto> getActorsByRole(String role) {
        log.info("Fetching all actors with role: {}", role);
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
