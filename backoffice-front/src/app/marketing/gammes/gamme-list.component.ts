import { Component, OnInit } from '@angular/core';
import { GammeService } from '../../../app/services/gamme.service';
import { Gamme } from '../../models/gamme.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import Swal from 'sweetalert2';
import { Modal } from 'bootstrap'; // Import bootstrap types (optionnel)

@Component({
  selector: 'app-gamme-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: 'gamme-list.component.html',
  styleUrls: ['./gamme-list.component.scss']
})
export class GammeListComponent implements OnInit {
    gammes: any[] = [];
    selectedGamme: any = {};
    isEdit: boolean = false;
  
    constructor(private gammeService: GammeService) {}
  
    ngOnInit(): void {
      this.loadGammes();
    }
  
    loadGammes() {
      this.gammeService.getAllGammes().subscribe(data => {
        this.gammes = data;
      });
    }
  
    openCreateModal() {
      this.isEdit = false;
      this.selectedGamme = { flag: true };
      this.openModal();
    }
  
    openEditModal(gamme: any) {
      this.isEdit = true;
      this.selectedGamme = { ...gamme };
      this.openModal();
    }
  
  openModal() {
    const modalEl = document.getElementById('gammeModal')!;
    // 1️⃣ Déplace la modal hors de ton layout, en fin de <body>
    document.body.appendChild(modalEl);
    // 2️⃣ Maintenant ouvre la modal
    new (window as any).bootstrap.Modal(modalEl).show();
  }

  closeModal() {
    const modalEl = document.getElementById('gammeModal')!;
    const modal = (window as any).bootstrap.Modal.getInstance(modalEl);
    modal.hide();
    // 3️⃣ (optionnel) remets la modal à sa place d’origine si tu veux garder le HTML sémantique
    document.querySelector('app-gamme-list .container')!.appendChild(modalEl);
  }
  
    onSubmit() {
      if (this.isEdit) {
        this.gammeService.updateGamme(this.selectedGamme).subscribe(() => {
          this.loadGammes();
          this.closeModal();
        });
      } else {
        this.gammeService.createGamme(this.selectedGamme).subscribe(() => {
          this.loadGammes();
          this.closeModal();
        });
      }
    }
  
    onToggleFlag(codeGamme: number | null) {
        if (codeGamme == null) {
          console.error("ID is null, cannot toggle flag");
          return;
        }
        this.gammeService.toggleFlag(codeGamme).subscribe({
          next: () => this.loadGammes(),
          error: (err) => console.error(err)
        });
      }
      
      trackByCodeGamme(index: number, gamme: any): string {
        return gamme.codeGamme;
      }
      
      confirmDelete(codeGamme: number) {
        Swal.fire({
          title: 'Are you sure?',
          text: 'This action cannot be undone!',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#d33',
          cancelButtonColor: '#6c757d',
          confirmButtonText: 'Yes, delete it!',
          cancelButtonText: 'Cancel'
        }).then((result) => {
          if (result.isConfirmed) {
            this.gammeService.deleteGamme(codeGamme).subscribe({
              next: () => {
                Swal.fire('Deleted!', 'The category has been deleted.', 'success');
                this.loadGammes();
              },
              error: (error) => {
                console.error(error);
                Swal.fire('Error', 'Could not delete the category. It may be in use.', 'error');
              }
            });
          }
        });
      }
      
      
  }