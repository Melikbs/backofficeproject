package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.CommandeDetailDTO;
import com.example.backofficeproject.Dto.LigneCommandeDTO;
import com.example.backofficeproject.Dto.LivraisonDTO;
import com.example.backofficeproject.job.LivraisonStatusUpdateJob;
import com.example.backofficeproject.mapper.CommandeMapper;
import com.example.backofficeproject.model.Commande;
import com.example.backofficeproject.model.Livraison;
import com.example.backofficeproject.model.StatusLivraison;
import com.example.backofficeproject.repositories.LivraisonRepository;
import com.example.backofficeproject.service.LivraisonService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
public class LivraisonController {

    @Autowired
    private LivraisonRepository livraisonRepository;

    @Autowired
    private LivraisonService livraisonService;

    @Autowired
    private CommandeMapper commandeMapper;

    @Autowired
    private Scheduler scheduler;  // ✅ for manual Quartz triggering

    @GetMapping
    public List<LivraisonDTO> getAll() {
        return livraisonService.getAll();
    }

    @GetMapping("/{id}")
    public LivraisonDTO getById(@PathVariable Long id) {
        return livraisonService.getById(id);
    }

    @PostMapping("/create-from-commande/{codeCommande}")
    public LivraisonDTO create(@PathVariable Long codeCommande) {
        return livraisonService.createFromCommande(codeCommande);
    }

    @GetMapping("/commande/{codeCommande}")
    public ResponseEntity<LivraisonDTO> getByCommandeCode(@PathVariable Long codeCommande) {
        LivraisonDTO livraison = livraisonService.getByCommandeCode(codeCommande);
        return ResponseEntity.ok(livraison);
    }

    @PostMapping("/{id}/start-tracking")
    public ResponseEntity<String> startTracking(@PathVariable Long id) throws SchedulerException {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison not found"));

        if (livraison.getStatusLivraison() != StatusLivraison.VALIDEE) {
            throw new IllegalStateException("Tracking can only start after validation.");
        }

        livraison.setStatusLivraison(StatusLivraison.COLLECTEE);
        livraisonRepository.save(livraison);

        // ✅ Schedule job for this specific Livraison
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("livraisonId", id);

        JobDetail jobDetail = JobBuilder.newJob(LivraisonStatusUpdateJob.class)
                .withIdentity("livraisonStatusJob_" + id, "livraison")
                .setJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_" + id, "livraison")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

        return ResponseEntity.ok("Tracking started for Livraison " + id);
    }

    @GetMapping("/{id}/commande")
    public ResponseEntity<CommandeDetailDTO> getCommandeByLivraison(@PathVariable Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        Commande commande = livraison.getCommande();

        List<LigneCommandeDTO> lignes = commande.getLignes().stream()
                .map(commandeMapper::toLigneDTO)
                .toList();

        return ResponseEntity.ok(commandeMapper.toDetailDTO(commande, lignes));
    }
    @PostMapping("/{id}/valider")
    public ResponseEntity<LivraisonDTO> validerLivraison(@PathVariable Long id) {
        LivraisonDTO validated = livraisonService.validerLivraison(id);
        return ResponseEntity.ok(validated);
    }

}
