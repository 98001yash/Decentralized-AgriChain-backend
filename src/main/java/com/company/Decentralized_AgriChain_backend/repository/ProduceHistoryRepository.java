package com.company.Decentralized_AgriChain_backend.repository;

import com.company.Decentralized_AgriChain_backend.enitites.Actor;
import com.company.Decentralized_AgriChain_backend.enitites.Produce;
import com.company.Decentralized_AgriChain_backend.enitites.ProduceHistory;
import com.company.Decentralized_AgriChain_backend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduceHistoryRepository extends JpaRepository<ProduceHistory, Long> {

    List<ProduceHistory> findByProduce(Produce produce);

    // count all history records by the status of produce
    long countByProduce_Status(Status status);

    // count all history records by owner of the produce
    long countByProduce_CurrentOwner(Actor owner);

    // optionally count by fromActor or toActor
    long countByFromActor(Actor actor);
    long countByToActor(Actor actor);
}
