package com.company.Decentralized_AgriChain_backend.repository;

import com.company.Decentralized_AgriChain_backend.enitites.Actor;
import com.company.Decentralized_AgriChain_backend.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor,Long> {

    Optional<Actor> findByWalletAddress(String walletAddress);

    List<Actor> findByRole(Role role);
}
