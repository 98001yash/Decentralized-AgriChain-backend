package com.company.Decentralized_AgriChain_backend.dtos;


import com.company.Decentralized_AgriChain_backend.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProduceDto {

    private Long id;
    private String name;
    private String description;
    private Long currentOwnerId;
    private Long quantity;
    private Status status;
}
