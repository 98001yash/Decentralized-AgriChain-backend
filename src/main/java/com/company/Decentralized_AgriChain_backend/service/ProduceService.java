package com.company.Decentralized_AgriChain_backend.service;

import com.company.Decentralized_AgriChain_backend.dtos.ProduceDto;
import com.company.Decentralized_AgriChain_backend.dtos.TransferProduceDto;

import java.util.List;

public interface ProduceService {

    ProduceDto addProduce(ProduceDto produceDto);
    ProduceDto getProduceById(Long id);
    List<ProduceDto> getProduceByOwner(Long actorId);
    ProduceDto transferProduce(TransferProduceDto transferProduceDto);
}
