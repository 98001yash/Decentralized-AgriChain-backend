package com.company.Decentralized_AgriChain_backend.enitites;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProduceHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "produce_id")
    private Produce produce;


    @ManyToOne
    @JoinColumn(name = "from_actor_id")
    private Actor fromActor;

    @ManyToOne
    @JoinColumn(name = "to_actor_id")
    private Actor toActor;


    private LocalDateTime transferredAt;

    private String status;
    private String reason;
    private String comments;
}
