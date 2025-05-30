import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Livraison, StatusLivraison } from 'src/app/models/livraison.model';
import { LivraisonService } from 'src/app/services/livraison.service';

@Component({
  selector: 'app-livraison-list',
  standalone: true,
  imports: [CommonModule,FormsModule,ReactiveFormsModule],
  templateUrl: './livraison-list.component.html',
  styleUrls: ['./livraison-list.component.scss']
})
export class LivraisonListComponent implements OnInit {
  livraisons: Livraison[] = [];
readonly statusTranslations: { [key: string]: string } = {
  'CREE': 'Created',
  'VALIDEE': 'Validated',
  'COLLECTEE': 'Collected',
  'EN_TRANSIT': 'In Transit',
  'ARRIVEE': 'Arrived',
  'LIVREE': 'Delivered'
};

  constructor(private livraisonService: LivraisonService,private router: Router,private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.loadLivraisons();
  }

  loadLivraisons() {
    this.livraisonService.getAll().subscribe({
      next: data => this.livraisons = data,
      error: err => console.error('Erreur lors du chargement des livraisons', err)
    });
  }

  getStatusClass(status: StatusLivraison): string {
    switch (status) {
      case 'CREE': return 'badge bg-secondary';
      case 'VALIDEE': return 'badge bg-primary';
      case 'COLLECTEE': return 'badge bg-warning';
      case 'EN_TRANSIT': return 'badge bg-info';
      case 'ARRIVEE': return 'badge bg-dark';
      case 'LIVREE': return 'badge bg-success';
      default: return 'badge bg-light';
    }
  }
  valider(id: number) {
    this.livraisonService.validerLivraison(id).subscribe({
      next: () => this.loadLivraisons(),
      error: err => console.error('Erreur validation livraison', err)
    });
  }
  
  details(id: number) {
    this.router.navigate(['/dashboard/logistique/livraisons', id]);

  }
  
}
