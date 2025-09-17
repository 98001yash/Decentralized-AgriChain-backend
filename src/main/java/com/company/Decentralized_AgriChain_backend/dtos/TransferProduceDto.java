package com.company.Decentralized_AgriChain_backend.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferProduceDto {

    private Long produceId;   // which produce to transfer
    private Long fromActorId;   // sender actorId
    private Long toActorId;   // receiver actorIs

    private String status;
    private String reason;
    private String comments;
}
