import { Component, OnInit } from '@angular/core';
import { MarqueService } from '../../../app/services/marque.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Marque } from 'src/app/models/marque.model';
import Swal from 'sweetalert2'; 
import { GammeService } from 'src/app/services/gamme.service';

import { Gamme } from '../../models/gamme.model'; // Assurez-vous que le chemin est correct

@Component({
  selector: 'app-marque-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './marque-list.component.html',
  styleUrls: ['./marque-list.component.scss']
})
export class MarqueListComponent implements OnInit {
  marques: Marque[] = [];
  gammes: Gamme[] = []; 
  selectedMarque: Marque = {} as Marque;
  isEdit = false;

  constructor(private marqueService: MarqueService,private gammeService : GammeService) {}

  ngOnInit(): void {
    this.loadMarques();
    this.getGammes(); // Charger les gammes au démarrage
  }
  getGammes(): void {
    this.gammeService.getAllGammes().subscribe(data => {
      this.gammes = data;  // Récupérer les gammes
    });
  }
  loadMarques() {
    this.marqueService.getAllMarques().subscribe({
      next: (data) => this.marques = data,
      error: (err) => console.error('Error loading marques:', err)
    });
  }

  openCreateModal(): void {
    this.selectedMarque = {
      codeMarque: 0,
      libelle: '',
      logo: '',
      actif: true,
      codeGamme: 0
    };
    this.isEdit = false;
    this.openModal();
  }
  

  openEditModal(marque: Marque) {
    this.isEdit = true;
    this.selectedMarque = { ...marque };
    this.openModal();
  }
openModal() {
    const modalEl = document.getElementById('marqueModal')!;
    document.body.appendChild(modalEl);
    new (window as any).bootstrap.Modal(modalEl).show();
  }

  closeModal() {
    const modalEl = document.getElementById('marqueModal')!;
    const modal = (window as any).bootstrap.Modal.getInstance(modalEl);
    modal.hide();
    document.querySelector('app-marque-list .container')!.appendChild(modalEl);
  }

  onSubmit(): void {
    if (this.isEdit) {
      this.marqueService.updateMarque(this.selectedMarque).subscribe({
        next: (createdMarque) => {
          this.marques.unshift(createdMarque); // Add directly if you trust the backend to return created object
          this.closeModal();
        }
        ,
        error: (err) => console.error('Error updating marque:', err)
      });
    } else {
      this.marqueService.createMarque(this.selectedMarque).subscribe({
        next: () => {
          this.loadMarques();
          this.closeModal();
        },
        error: (err) => console.error('Error creating marque:', err)
      });
    }
  
    // Reset form after submit
    this.selectedMarque = {
      codeMarque: 0,
      libelle: '',
      logo: '',
      actif: true,
      codeGamme: 0
    };
    this.isEdit = false;
  }
  
  

  deleteMarque(codeMarque: number): void {
    Swal.fire({
      title: 'Are you sure?',
      text: 'This action will permanently delete the brand.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.marqueService.deleteMarque(codeMarque).subscribe({
          next: () => {
            Swal.fire('Deleted!', 'The brand has been successfully deleted.', 'success');
            this.loadMarques();
          },
          error: (err) => {
            console.error('Error deleting marque:', err);
            Swal.fire('Error', 'Failed to delete the brand. It may be used elsewhere.', 'error');
          }
        });
      }
    });
  }

  onToggleActif(marque: Marque): void {
    const previousActif = marque.actif; // On garde l'ancien état au cas où
    marque.actif = !marque.actif; // On change directement dans l'interface
  
    this.marqueService.toggleActif(marque.codeMarque).subscribe({
      next: () => {
        console.log('Actif toggled successfully');
        // Pas besoin de recharger toute la liste
      },
      error: (err) => {
        console.error('Failed to toggle actif', err);
        marque.actif = previousActif; // On annule le changement si erreur
      }
    });
  }
  
  
  
  

  trackByCodeMarque(index: number, marque: Marque): number {
    return marque.codeMarque;
  }
}
