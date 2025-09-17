package com.company.Decentralized_AgriChain_backend.service.Impl;


import com.company.Decentralized_AgriChain_backend.dtos.ProduceDto;
import com.company.Decentralized_AgriChain_backend.dtos.ProduceHistoryDto;
import com.company.Decentralized_AgriChain_backend.dtos.TransferProduceDto;
import com.company.Decentralized_AgriChain_backend.enitites.Actor;
import com.company.Decentralized_AgriChain_backend.enitites.Produce;
import com.company.Decentralized_AgriChain_backend.enitites.ProduceHistory;
import com.company.Decentralized_AgriChain_backend.enums.Role;
import com.company.Decentralized_AgriChain_backend.enums.Status;
import com.company.Decentralized_AgriChain_backend.exception.ResourceNotFoundException;
import com.company.Decentralized_AgriChain_backend.repository.ActorRepository;
import com.company.Decentralized_AgriChain_backend.repository.ProduceHistoryRepository;
import com.company.Decentralized_AgriChain_backend.repository.ProduceRepository;
import com.company.Decentralized_AgriChain_backend.service.ProduceService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j

public class ProduceServiceImpl implements ProduceService {

    private final ProduceRepository produceRepository;
    private final ActorRepository actorRepository;
    private ProduceHistoryRepository produceHistoryRepository;
    private final ModelMapper modelMapper;

    private final Web3j web3j;
    private final Credentials credentials;
    private final String contractAddress;

    public ProduceServiceImpl(ProduceRepository produceRepository,
                              ActorRepository actorRepository,
                              ModelMapper modelMapper,
                              ProduceHistoryRepository produceHistoryRepository,
                              @Value("${web3.rpcUrl}") String rpcUrl,
                              @Value("${web3.privateKey}") String privateKey,
                              @Value("${web3.contractAddress}") String contractAddress) {
        this.produceRepository = produceRepository;
        this.actorRepository = actorRepository;
        this.modelMapper = modelMapper;
        this.produceHistoryRepository = produceHistoryRepository;
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.credentials = Credentials.create(privateKey);
        this.contractAddress = contractAddress;
    }

    @Override
    public ProduceDto addProduce(ProduceDto produceDto) {
        log.info("Adding new produce: {}", produceDto.getName());

        Actor owner = actorRepository.findById(produceDto.getCurrentOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + produceDto.getCurrentOwnerId()));

        Produce produce = modelMapper.map(produceDto, Produce.class);
        produce.setCurrentOwner(owner);
        produce.setStatus(Status.FARMER);
        produce.setAddedAt(LocalDateTime.now());

        Produce saved = produceRepository.save(produce);
        log.debug("Produce saved locally with ID: {}", saved.getId());

        // Blockchain call
        try {
            String encodedFunction = org.web3j.abi.FunctionEncoder.encode(
                    new org.web3j.abi.datatypes.Function(
                            "addProduce",
                            List.of(
                                    new org.web3j.abi.datatypes.Utf8String(produce.getName()),
                                    new org.web3j.abi.datatypes.Utf8String(produce.getDescription()),
                                    new org.web3j.abi.datatypes.generated.Uint256(produce.getQuantity())
                            ),
                            List.of()
                    )
            );

            EthSendTransaction txResponse = web3j.ethSendTransaction(
                    Transaction.createFunctionCallTransaction(
                            credentials.getAddress(),
                            null,
                            DefaultGasProvider.GAS_PRICE,
                            DefaultGasProvider.GAS_LIMIT,
                            contractAddress,
                            encodedFunction
                    )
            ).send();

            if (txResponse.hasError()) {
                log.error("Blockchain transaction failed: {}", txResponse.getError().getMessage());
                throw new RuntimeException("Blockchain transaction failed: " + txResponse.getError().getMessage());
            }

            log.info("Produce added on blockchain, tx hash: {}", txResponse.getTransactionHash());

        } catch (Exception e) {
            log.error("Error while adding produce to blockchain", e);
            throw new RuntimeException("Blockchain integration failed", e);
        }

        return modelMapper.map(saved, ProduceDto.class);
    }

    @Override
    public ProduceDto getProduceById(Long id) {
        log.info("Fetching produce with id: {}", id);
        Produce produce = produceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return modelMapper.map(produce, ProduceDto.class);
    }

    @Override
    public List<ProduceDto> getProduceByOwner(Long actorId) {
        log.info("Fetching produce for owner {}", actorId);
        Actor owner = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + actorId));

        return produceRepository.findByCurrentOwner(owner)
                .stream()
                .map(produce -> modelMapper.map(produce, ProduceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProduceDto transferProduce(TransferProduceDto transferProduceDto) {
        log.info("Transferring produce ID {} from actor ID {} to actor ID {}",
                transferProduceDto.getProduceId(), transferProduceDto.getFromActorId(), transferProduceDto.getToActorId());

        // Fetch produce and actors
        Produce produce = produceRepository.findById(transferProduceDto.getProduceId())
                .orElseThrow(() -> new ResourceNotFoundException("Produce not found with ID: " + transferProduceDto.getProduceId()));

        Actor fromActor = actorRepository.findById(transferProduceDto.getFromActorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with ID: " + transferProduceDto.getFromActorId()));

        Actor toActor = actorRepository.findById(transferProduceDto.getToActorId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with ID: " + transferProduceDto.getToActorId()));

        if (!produce.getCurrentOwner().getId().equals(fromActor.getId())) {
            throw new ResourceNotFoundException("Produce is not owned by actor ID: " + fromActor.getId());
        }

        // Update owner + status
        produce.setCurrentOwner(toActor);
        switch (toActor.getRole()) {
            case DISTRIBUTOR -> produce.setStatus(Status.DISTRIBUTOR);
            case RETAILER -> produce.setStatus(Status.RETAILER);
            default -> produce.setStatus(Status.SOLD);
        }

        Produce saved = produceRepository.save(produce);

        ProduceHistory history = new ProduceHistory();
        history.setProduce(saved);
        history.setFromActor(fromActor);
        history.setToActor(toActor);
        history.setTransferredAt(LocalDateTime.now());
        produceHistoryRepository.save(history);

        // Blockchain transfer (unchanged)
        try {
            String functionName = toActor.getRole() == Role.DISTRIBUTOR ? "transferToDistributor" : "transferToRetailer";

            org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                    functionName,
                    List.of(
                            new Uint256(produce.getId()),
                            new Address(toActor.getWalletAddress())
                    ),
                    List.of()
            );

            String encodedFunction = org.web3j.abi.FunctionEncoder.encode(function);

            EthSendTransaction txResponse = web3j.ethSendTransaction(
                    Transaction.createFunctionCallTransaction(
                            credentials.getAddress(),
                            null,
                            DefaultGasProvider.GAS_PRICE,
                            DefaultGasProvider.GAS_LIMIT,
                            contractAddress,
                            encodedFunction
                    )
            ).send();

            if (txResponse.hasError()) {
                log.error("Blockchain transaction failed: {}", txResponse.getError().getMessage());
                throw new RuntimeException("Blockchain transaction failed: " + txResponse.getError().getMessage());
            }

            log.info("Produce transferred on blockchain, tx hash: {}", txResponse.getTransactionHash());

        } catch (Exception e) {
            log.error("Error while transferring produce on blockchain", e);
            throw new RuntimeException("Blockchain integration failed: " + e.getMessage(), e);
        }

        return modelMapper.map(saved, ProduceDto.class);
    }

    @Override
    public List<ProduceDto> getAllProduces() {
        return produceRepository.findAll()
                .stream()
                .map(produce -> modelMapper.map(produce, ProduceDto.class))
                .collect(Collectors.toList());
    }


    // to Get the History of the Product
    @Override
    public List<ProduceHistoryDto> getProduceHistory(Long produceId) {
        // Find the produce
        Produce produce = produceRepository.findById(produceId)
                .orElseThrow(() -> new RuntimeException("Produce not found with id: " + produceId));

        // Fetch all history records for this produce
        List<ProduceHistory> histories = produceHistoryRepository.findByProduce(produce);

        // Map to DTOs
        return histories.stream()
                .map(h -> ProduceHistoryDto.builder()
                        .fromActorName(h.getFromActor() != null ? h.getFromActor().getName() : "Origin")
                        .toActorName(h.getToActor() != null ? h.getToActor().getName() : "Unknown")
                        .transferredAt(h.getTransferredAt())
                        .build())
                .collect(Collectors.toList());
    }



    @Override
    public long getTotalProducesCount() {
        return produceRepository.count();
    }

    @Override
    public long getProducesCountByStatus(Status status) {
        return produceRepository.countByStatus(status);
    }

    @Override
    public long getProducesCountByOwner(Long ownerId) {
        Actor owner = actorRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        return produceRepository.countByCurrentOwner(owner);
    }

}
