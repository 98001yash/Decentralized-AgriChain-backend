package com.company.Decentralized_AgriChain_backend.service;

import com.company.Decentralized_AgriChain_backend.dtos.ProduceDto;
import com.company.Decentralized_AgriChain_backend.dtos.ProduceHistoryDto;
import com.company.Decentralized_AgriChain_backend.dtos.TransferProduceDto;
import com.company.Decentralized_AgriChain_backend.enums.Status;

import java.util.List;

public interface ProduceService {

    ProduceDto addProduce(ProduceDto produceDto);
    ProduceDto getProduceById(Long id);
    List<ProduceDto> getProduceByOwner(Long actorId);
    ProduceDto transferProduce(TransferProduceDto transferProduceDto);

    List<ProduceDto> getAllProduces();

    List<ProduceHistoryDto> getProduceHistory(Long produceId);

    long getTotalProducesCount();
    long getProducesCountByStatus(Status status);
    long getProducesCountByOwner(Long ownerId);


}
