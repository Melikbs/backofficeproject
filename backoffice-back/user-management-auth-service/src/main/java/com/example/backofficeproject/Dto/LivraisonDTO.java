package com.example.backofficeproject.Dto;

import com.example.backofficeproject.model.StatusLivraison;
import lombok.*;

import java.util.Date;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LivraisonDTO {
    private Long codeLivraison;
    private String numTracking;
    private Date dateLivraison;
    private String shippingLabel;
    private StatusLivraison statusLivraison;
    private Long codeCommande;
}
