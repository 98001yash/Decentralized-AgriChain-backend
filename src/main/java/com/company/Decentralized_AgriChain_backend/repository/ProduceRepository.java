package com.company.Decentralized_AgriChain_backend.repository;

import com.company.Decentralized_AgriChain_backend.enitites.Actor;
import com.company.Decentralized_AgriChain_backend.enitites.Produce;
import com.company.Decentralized_AgriChain_backend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduceRepository extends JpaRepository<Produce, Long> {

    List<Produce> findByCurrentOwner(Actor actor);
    long countByStatus(Status status);
    long countByCurrentOwner(Actor owner);

}
