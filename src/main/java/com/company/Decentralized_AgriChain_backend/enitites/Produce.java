package com.company.Decentralized_AgriChain_backend.enitites;


import com.company.Decentralized_AgriChain_backend.enums.Status;
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
public class Produce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private String description;
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "current_owner_id")
    private Actor currentOwner;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime addedAt;
}
