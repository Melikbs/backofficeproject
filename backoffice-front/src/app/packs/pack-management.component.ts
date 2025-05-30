import { Component, OnInit } from '@angular/core';
import { PackService } from '../services/pack.service';
import { Pack, PackProduit } from '../models/pack.model';
import { ProduitService } from '../services/produit.service';
import { ProduitResponse } from '../models/produitresponse.model';
import { AuthService } from '../services/auth.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { Router, RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

interface PackWithDetails extends Pack {
  showDetails: boolean;
}

@Component({
  selector: 'app-pack-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './pack-management.component.html',
  styleUrls: ['./pack-management.component.scss']
})
export class PackManagementComponent implements OnInit {
  packs: PackWithDetails[] = [];
  produits: ProduitResponse[] = [];
  isMarketing = false;
  selectedForPack: PackProduit[] = [];

  constructor(
    private packService: PackService,
    private produitService: ProduitService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.isMarketing = this.authService.getUserRole() === 'MARKETING';
    this.loadPacks();
    this.loadProduits();
  }

  private loadPacks() {
    this.packService.getAll().subscribe(data => {
      // on initialise showDetails à false
      this.packs = data.map(p => ({ ...p, showDetails: false }));
    });
  }

  private loadProduits() {
    this.produitService.getAllProduits().subscribe(data => {
      this.produits = data;
    });
  }

  /** Bascule l’affichage des détails pour un pack donné */
  toggleDetails(pack: PackWithDetails) {
    pack.showDetails = !pack.showDetails;
  }

  /** Ouvre le modal de création via SweetAlert2 */
  async openCreateModal(): Promise<void> {
    const { value: confirmed } = await Swal.fire({
      title: 'Créer un Pack',
      html: `
        <select id="product-select" class="swal2-select">
          <option value="">-- Choisir un produit --</option>
          ${this.produits.map(p => `<option value="${p.codeProduit}">${p.libelle}</option>`).join('')}
        </select>
        <input id="reduc-percent" type="number" class="swal2-input" placeholder="% réduction">
        <input id="reduc-value" type="number" class="swal2-input" placeholder="€ réduction">
        <button type="button" id="add-to-pack" class="swal2-confirm swal2-styled">Ajouter au pack</button>
        <div id="preview-zone" style="margin-top: 1rem; text-align:left;"></div>
      `,
      showCancelButton: true,
      confirmButtonText: 'Créer le pack',
      preConfirm: () => ({
        nom: (document.getElementById('swal2-title') as HTMLElement)?.textContent || '',
        produits: this.selectedForPack
      }),
      didOpen: () => this.setupCreateListeners()
    });

    if (confirmed) {
      const newPack: Pack = {
        nom: 'Pack temporaire',
        description: '',
        actif: true,
        produits: this.selectedForPack
      };
      this.packService.create(newPack).subscribe(() => this.loadPacks());
    }
  }

  private setupCreateListeners() {
    this.selectedForPack = [];
    const select = document.getElementById('product-select') as HTMLSelectElement;
    const percentInput = document.getElementById('reduc-percent') as HTMLInputElement;
    const valueInput = document.getElementById('reduc-value') as HTMLInputElement;
    const preview = document.getElementById('preview-zone');
    const addBtn = document.getElementById('add-to-pack');

    addBtn?.addEventListener('click', () => {
      const code = parseInt(select.value, 10);
      const produit = this.produits.find(p => p.codeProduit === code);
      const pourcent = parseFloat(percentInput.value);
      const valeur = parseFloat(valueInput.value);

      if (produit && !isNaN(pourcent) && !isNaN(valeur)) {
        this.selectedForPack.push({ codeProduit: code, reductionPourcentage: pourcent, reductionValeur: valeur });

        if (preview) {
          const item = document.createElement('div');
          item.innerHTML = `<strong>${produit.libelle}</strong> - ${pourcent}% / ${valeur}€ 
            <button data-code="${code}" style="margin-left:10px;">❌</button>`;
          preview.appendChild(item);
          item.querySelector('button')?.addEventListener('click', (e: any) => {
            const codeToRemove = parseInt(e.target.dataset.code, 10);
            this.selectedForPack = this.selectedForPack.filter(p => p.codeProduit !== codeToRemove);
            item.remove();
          });
        }
        select.value = '';
        percentInput.value = '';
        valueInput.value = '';
      }
    });
  }

  /** Retourne l’URL de l’image produit */
  getProduitImage(codeProduit: number): string {
    const produit = this.produits.find(p => p.codeProduit === codeProduit);
    return produit?.image || 'placeholder.jpg';
  }

  /** Supprime un pack après confirmation navigateur */
  async deletePack(id: number) {
  const result = await Swal.fire({
    title: 'Are you sure?',
    text: 'This pack will be permanently deleted.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc3545',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Yes, delete it!',
    cancelButtonText: 'Cancel'
  });

  if (result.isConfirmed) {
    this.packService.delete(id).subscribe(() => {
      this.loadPacks();
      Swal.fire({
        title: 'Deleted!',
        text: 'The pack has been deleted.',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false
      });
    });
  }
}


  /** Active / désactive localement et en back */
  togglePackActif(id: number) {
    this.packService.toggleActif(id).subscribe(() => {
      const pack = this.packs.find(p => p.id === id);
      if (pack) { pack.actif = !pack.actif; }
    });
  }

  /** Navigation vers l’édition */
  editPack(pack: Pack) {
    this.router.navigate(['/dashboard/marketing/packs/edit', pack.id]);
  }

  /** Helpers prix et labels */
  getProductLabel(codeProduit: number): string {
    const p = this.produits.find(x => x.codeProduit === codeProduit);
    return p ? p.libelle : 'Unknown';
  }
  getOriginalPrice(codeProduit: number): number {
    const p = this.produits.find(x => x.codeProduit === codeProduit);
    return p ? p.prix : 0;
  }
  calculateDiscountedPrice(p: { codeProduit: number; reductionPourcentage?: number; reductionValeur?: number; }): number {
    const orig = this.getOriginalPrice(p.codeProduit);
    if (p.reductionPourcentage && p.reductionPourcentage > 0) {
      return orig * (1 - p.reductionPourcentage / 100);
    }
    if (p.reductionValeur && p.reductionValeur > 0) {
      return orig - p.reductionValeur;
    }
    return orig;
  }
  calculateTotalPackPrice(prods: { codeProduit: number; reductionPourcentage?: number; reductionValeur?: number; }[]): number {
    return prods.reduce((sum, x) => sum + this.calculateDiscountedPrice(x), 0);
  }
}