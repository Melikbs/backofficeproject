import { Component, OnInit } from '@angular/core';
import { ProduitResponse } from '../../models/produitresponse.model';
import { PackService } from '../../services/pack.service';
import { ProduitService } from '../../services/produit.service';
import { GammeService } from '../../services/gamme.service';
import { MarqueService } from '../../services/marque.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Pack } from '../../models/pack.model';
import Swal from 'sweetalert2';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-pack-creation',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './pack-creation.component.html',
  styleUrls: ['./pack-creation.component.scss']
})
export class PackCreationComponent implements OnInit {
  produits: ProduitResponse[] = [];
  selectedForPack: { codeProduit: number; reductionValeur: number; type: 'DT' | 'PERCENT' }[] = [];
  nom = '';
  description = '';
  searchTerm: string = '';
  selectedCategory: string = '';
  selectedBrand: string = '';
  gammes: any[] = [];
  marques: any[] = [];
  editingPackId: number | null = null;

  constructor(
    private produitService: ProduitService,
    private packService: PackService,
    private gammeService: GammeService,
    private marqueService: MarqueService,
    private route: ActivatedRoute
  ) {}
  
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.loadPackForEdit(+id);
      }
    });
    this.produitService.getAllProduits().subscribe(data => this.produits = data);
    this.gammeService.getAllGammes().subscribe(data => this.gammes = data);
    this.marqueService.getAllMarques().subscribe(data => this.marques = data);
  }
  filteredProduits(): ProduitResponse[] {
    return this.produits.filter(p =>
      (!this.searchTerm || p.libelle.toLowerCase().includes(this.searchTerm.toLowerCase())) &&
      (!this.selectedCategory || p.codeGamme == +this.selectedCategory) &&
      (!this.selectedBrand || p.codeMarque == +this.selectedBrand)
    );
  }
  loadPackForEdit(id: number) {
    this.editingPackId = id;

    this.packService.getAll().subscribe(packs => {
      const pack = packs.find(p => p.id === id);
      if (pack) {
        this.nom = pack.nom;
        this.description = pack.description;
        this.selectedForPack = pack.produits.map(p => ({
          codeProduit: p.codeProduit,
          reductionValeur: p.reductionValeur || p.reductionPourcentage,
          type: p.reductionValeur > 0 ? 'DT' : 'PERCENT'
        }));
      }
    });
  }
  
  addProduit(produit: ProduitResponse): void {
    if (this.selectedForPack.some(p => p.codeProduit === produit.codeProduit)) return;
    this.selectedForPack.push({
      codeProduit: produit.codeProduit,
      reductionValeur: 0,
      type: 'DT'
    });
  }

  removeProduit(codeProduit: number): void {
    this.selectedForPack = this.selectedForPack.filter(p => p.codeProduit !== codeProduit);
  }

  getProductLibelle(codeProduit: number): string {
    return this.produits.find(p => p.codeProduit === codeProduit)?.libelle || 'Unknown';
  }

  submitPack(): void {
    if (!this.nom.trim() || !this.description.trim()) {
      Swal.fire({
        icon: 'warning',
        title: 'Missing Information',
        text: 'Please provide both a pack name and a description.',
        confirmButtonColor: '#e74c3c',
        background: '#f8f9fa',
        color: '#343a40'
      });
      return;
    }
  
    if (this.selectedForPack.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No Products Selected',
        text: 'Please select at least one product for this pack.',
        confirmButtonColor: '#e74c3c',
        background: '#f8f9fa',
        color: '#343a40'
      });
      return;
    }
  
    const pack: Pack = {
      id: this.editingPackId ?? undefined,
      nom: this.nom,
      description: this.description,
      actif: true,
      produits: this.selectedForPack.map(p => ({
        codeProduit: p.codeProduit,
        reductionPourcentage: p.type === 'PERCENT' ? p.reductionValeur : 0,
        reductionValeur: p.type === 'DT' ? p.reductionValeur : 0
      }))
    };
  
    const request$ = this.editingPackId
      ? this.packService.update(pack)
      : this.packService.create(pack);
  
    request$.subscribe(() => {
      Swal.fire({
        icon: 'success',
        title: this.editingPackId ? 'Pack updated!' : 'Pack created!',
        confirmButtonColor: '#e74c3c',
        background: '#f8f9fa',
        color: '#343a40'
      });
      this.goBack(); // or use router.navigate
    });
  }

  goBack(): void {
    history.back();
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.selectedCategory = '';
    this.selectedBrand = '';
  }
}
