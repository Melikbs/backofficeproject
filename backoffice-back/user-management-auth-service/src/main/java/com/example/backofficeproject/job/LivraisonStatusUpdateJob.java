package com.example.backofficeproject.job;

import com.example.backofficeproject.model.Livraison;
import com.example.backofficeproject.model.StatusLivraison;
import com.example.backofficeproject.repositories.LivraisonRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LivraisonStatusUpdateJob implements Job {

    @Autowired
    private LivraisonRepository livraisonRepo;

    @Override
    public void execute(JobExecutionContext context) {
        Long livraisonId = context.getJobDetail().getJobDataMap().getLong("livraisonId");

        Livraison livraison = livraisonRepo.findById(livraisonId).orElse(null);
        if (livraison == null || livraison.getStatusLivraison() == StatusLivraison.LIVREE) return;

        StatusLivraison next = getNextStatus(livraison.getStatusLivraison());
        if (next != null) {
            livraison.setStatusLivraison(next);
            livraisonRepo.save(livraison);
            System.out.println("🚚 Livraison " + livraison.getCodeLivraison() + " → " + next);
        }
    }

    private StatusLivraison getNextStatus(StatusLivraison current) {
        return switch (current) {
            case COLLECTEE -> StatusLivraison.EN_TRANSIT;
            case EN_TRANSIT -> StatusLivraison.ARRIVEE;
            case ARRIVEE -> StatusLivraison.LIVREE;
            default -> null;
        };
    }
}
