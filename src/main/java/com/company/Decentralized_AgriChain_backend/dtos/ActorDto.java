package com.company.Decentralized_AgriChain_backend.dtos;


import com.company.Decentralized_AgriChain_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActorDto {

    private Long id;
    private String name;
    private Role role;
    private String walletAddress;
}
