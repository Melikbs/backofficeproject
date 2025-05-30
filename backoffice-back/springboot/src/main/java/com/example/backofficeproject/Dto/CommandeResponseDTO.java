package com.example.backofficeproject.Dto;

import com.example.backofficeproject.model.CommandeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeResponseDTO {
    private Long codeCommande;
    private Date dateCommande;
    private CommandeStatus status;

    private String clientNom;
    private String clientPrenom;
}
