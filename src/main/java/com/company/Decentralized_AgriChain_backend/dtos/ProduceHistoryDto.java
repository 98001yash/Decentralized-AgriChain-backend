package com.company.Decentralized_AgriChain_backend.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProduceHistoryDto {

    private String fromActorName;
    private String toActorName;
    private LocalDateTime transferredAt;

    private String status;
    private String reason;
    private String comments;
}
