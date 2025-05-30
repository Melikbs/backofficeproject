import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProduitService } from 'src/app/services/produit.service';
import { ProduitResponse } from 'src/app/models/produitresponse.model';

@Component({
  selector: 'app-competitive-analysis',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './competitive-analysis.component.html',
  styleUrls: ['./competitive-analysis.component.scss']
})
export class CompetitiveAnalysisComponent implements OnInit {
  productId: number | null = null;
  products: ProduitResponse[] = [];
  result: any = null;
  loading = false;
  error: string | null = null;
bestPrice: string | null = null;
  constructor(
    private http: HttpClient,
    private produitService: ProduitService
  ) {}

  ngOnInit(): void {
    this.produitService.getAllProduits().subscribe({
      next: (data) => {
        this.products = data;
        console.log('✅ Produits chargés :', this.products);
      },
      error: () => {
        this.error = '❌ Erreur lors du chargement des produits.';
      }
    });
  }

 runAnalysis(): void {
  if (!this.productId) return;

  this.loading = true;
  this.error = null;
  this.result = null;
  this.bestPrice = null;

  this.http.get<any>(`http://localhost:8080/api/ai/compare/${this.productId}`).subscribe({
    next: (data) => {
      this.result = data;
      this.loading = false;

      const prices = data.results
        .map((r: any) => parseFloat(r.price.replace(/[^\d.]/g, '').replace(',', '.')))
        .filter((n: number) => !isNaN(n));
      const min = Math.min(...prices);

      const best = data.results.find((r: any) => parseFloat(r.price.replace(/[^\d.]/g, '').replace(',', '.')) === min);
      this.bestPrice = best?.price;
    },
    error: () => {
      this.error = "Erreur lors de l'analyse.";
      this.loading = false;
    }
  });
}
}