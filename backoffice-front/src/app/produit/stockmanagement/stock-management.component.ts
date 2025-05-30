import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Stock } from 'src/app/models/stock.model';
import { StockService } from 'src/app/services/stock.service';

import { ProduitService } from 'src/app/services/produit.service';
import { ProduitResponse } from 'src/app/models/produitresponse.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CouleurService } from '../../services/couleur.service';  // Import du service de couleurs
import { Couleur } from '../../models/couleur.model'; 
import Swal from 'sweetalert2';

@Component({
  selector: 'app-stock-management',
  standalone:true,
  imports:[CommonModule,FormsModule,RouterModule],
  templateUrl: './stock-management.component.html',
  styleUrls: ['./stock-management.component.scss'],
 
})
export class StockManagementComponent implements OnInit {

  codeProduit!: number;
  produit!: ProduitResponse;
  stocks: Stock[] = [];
  couleurs: Couleur[] = [];  // Liste des couleurs
  showAddForm = false;  // Flag to show/hide the form
  newStock: Stock = {
    codeCouleur: 0, nombreStock: 0, inStock: true,
    codeStock: 0,
    codeProduit: 0,
    libelleCouleur: ''
  };  // New stock data

  constructor(
    private route: ActivatedRoute,
    private stockService: StockService,
    private produitService: ProduitService,
    private couleurService: CouleurService  // Injection du service CouleurService
  ) {}

  ngOnInit(): void {
    this.codeProduit = Number(this.route.snapshot.paramMap.get('codeProduit'));
    this.loadProduit();
    this.loadStocks();
    this.loadCouleurs();  // Charge les couleurs depuis l'API
  }

  loadProduit(): void {
    this.produitService.getProduitById(this.codeProduit).subscribe({
      next: (data) => this.produit = data,
      error: (err) => console.error(err)
    });
  }
  openAddStockSwal() {
    Swal.fire({
      title: 'Ajouter un stock',
      html:
        `<select id="select-couleur" class="swal2-select">
          <option value="">-- Sélectionner une couleur --</option>
          ${this.couleurs.map(couleur => `<option value="${couleur.codeCouleur}">${couleur.libelle}</option>`).join('')}
        </select>` +
        '<input id="input-quantite" type="number" min="1" class="swal2-input" placeholder="Quantité">',
      focusConfirm: false,
      confirmButtonText: 'Ajouter',
      showCancelButton: true,
      cancelButtonText: 'Annuler',
      confirmButtonColor: '#28a745',
      cancelButtonColor: '#d33',
      preConfirm: () => {
        const selectElement = document.getElementById('select-couleur') as HTMLSelectElement;
        const inputElement = document.getElementById('input-quantite') as HTMLInputElement;
        const codeCouleur = Number(selectElement.value);
        const quantite = Number(inputElement.value);
  
        if (!codeCouleur || !quantite || quantite <= 0) {
          Swal.showValidationMessage('Veuillez sélectionner une couleur et entrer une quantité valide.');
          return;
        }
  
        return { codeCouleur, quantite };
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        const stockToAdd: Stock = {
          codeStock: 0, // Il sera généré par la BDD
          codeProduit: this.codeProduit, // le produit courant
          codeCouleur: result.value.codeCouleur,
          nombreStock: result.value.quantite,
          inStock: result.value.quantite > 0,
          libelleCouleur: this.getCouleurLibelle(result.value.codeCouleur)
        };
  
        this.stockService.addStock(stockToAdd).subscribe({
          next: (newStock) => {
            this.stocks.push(newStock); // Ajouter directement dans le tableau
            Swal.fire({
              icon: 'success',
              title: 'Succès',
              text: 'Le stock a été ajouté avec succès.',
              timer: 1500,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error(err);
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Impossible d\'ajouter le stock.'
            });
          }
        });
      }
    });
  }
  
  loadStocks(): void {
    this.stockService.getStocksByProduit(this.codeProduit).subscribe({
      next: (data) => this.stocks = data,
      error: (err) => console.error(err)
    });
  }

  loadCouleurs(): void {
    this.couleurService.getCouleurs().subscribe({
      next: (data) => this.couleurs = data,
      error: (err) => console.error(err)
    });
  }

  openAddStockForm(): void {
    this.showAddForm = true;  // Show the add stock form
  }

  cancelAddStock(): void {
    this.showAddForm = false;  // Hide the form without saving
  }

  addStock(): void {
    if (this.newStock.codeCouleur && this.newStock.nombreStock > 0) {
      // 💥 Fix ici : affecter le vrai codeProduit
      this.newStock.codeProduit = this.codeProduit;
      
      console.log('Stock envoyé :', this.newStock);
  
      this.stockService.addStock(this.newStock).subscribe({
        next: (newStock) => {
          this.stocks.push(newStock);
          this.showAddForm = false;
        },
        error: (err) => console.error(err)
      });
    }
    Swal.fire({
      icon: 'success',
      title: 'Ajouté!',
      text: 'Le stock a été ajouté avec succès.',
      timer: 1500,
      showConfirmButton: false
    });
  }
  

  updateStock(stock: Stock): void {
    this.stockService.updateStock(stock).subscribe({
      next: () => {
        this.loadStocks();  // Optionnel : reload stocks after update to show changes
      },
      error: (err) => console.error(err)
    });
  }

  updateNombreStock(stock: Stock, event: any): void {
    const newValue = +event.target.value;  // Assure que la valeur est un nombre
    if (newValue >= 0) {
      stock.nombreStock = newValue;
      stock.inStock = newValue > 0;
      this.updateStock(stock);
    } else {
      event.target.value = stock.nombreStock;  // Restore old value if invalid input
    }
  }

  getCouleurLibelle(codeCouleur: number): string {
    return this.couleurs?.find(c => c.codeCouleur === codeCouleur)?.libelle || 'N/A';
  }
}