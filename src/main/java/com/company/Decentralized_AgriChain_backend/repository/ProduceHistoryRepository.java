package com.company.Decentralized_AgriChain_backend.repository;

import com.company.Decentralized_AgriChain_backend.enitites.Produce;
import com.company.Decentralized_AgriChain_backend.enitites.ProduceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduceHistoryRepository extends JpaRepository<ProduceHistory, Long>{
    List<ProduceHistory> findByProduce(Produce produce);
}
