import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { LivraisonService } from 'src/app/services/livraison.service';
import { Livraison } from 'src/app/models/livraison.model';
import { CommandeDetail } from 'src/app/models/commande-detail.model';
import JsBarcode from 'jsbarcode';

import * as pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';


(pdfMake as any).vfs = pdfFonts.vfs;


@Component({
  selector: 'app-livraison-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './livraison-detail.component.html',
  styleUrls: ['./livraison-detail.component.scss'],
})
export class LivraisonDetailComponent implements OnInit {
  livraisonId!: number;
  livraison?: Livraison;
  commande?: CommandeDetail;
  loading = true;
  pdfUrl?: SafeResourceUrl;

  trackingSteps = ['CREE', 'VALIDEE', 'COLLECTEE', 'EN_TRANSIT', 'ARRIVEE', 'LIVREE'];
// Ajoute ça en haut du composant (après les imports ou juste sous trackingSteps)
readonly statusTranslations: { [key: string]: string } = {
  'CREE': 'Created',
  'VALIDEE': 'Validated',
  'COLLECTEE': 'Collected',
  'EN_TRANSIT': 'In Transit',
  'ARRIVEE': 'Arrived',
  'LIVREE': 'Delivered'
};

  constructor(
    private route: ActivatedRoute,
    private livraisonService: LivraisonService,
    private http: HttpClient,
    private sanitizer: DomSanitizer

  ) {}

  ngOnInit(): void {
    this.livraisonId = +this.route.snapshot.paramMap.get('id')!;
    this.loadLivraison();
  
    // Polling every 5 seconds
    setInterval(() => {
      this.loadLivraison();  // only refresh livraison data
    }, 5000);
  }
  

  loadLivraison() {
    this.livraisonService.getById(this.livraisonId).subscribe({
      next: l => {
        this.livraison = l;
        this.loadCommande();
      },
      error: err => console.error(err),
    });
  }

  loadCommande() {
    this.http.get<CommandeDetail>(`http://localhost:8080/api/livraisons/${this.livraisonId}/commande`)
    .subscribe({
      next: c => {
        console.log(c); // Check what's returned
        this.commande = c;
      },
      error: err => console.error(err),
      complete: () => this.loading = false
    });
  
  }

  getCurrentStepIndex(): number {
    return this.livraison ? this.trackingSteps.indexOf(this.livraison.statusLivraison) : -1;
  }

  isStepActive(index: number): boolean {
    return index <= this.getCurrentStepIndex();
  }

  getStatusClass(status: string | undefined): string {
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

  startTracking() {
    if (!this.livraison) return;
    this.livraisonService.startTracking(this.livraison.codeLivraison).subscribe({
      next: () => this.loadLivraison(),
      error: err => console.error(err),
    });
  }
  generateLabel(livraison: Livraison, commande: CommandeDetail): void {
    const barcodeValue = livraison.numTracking || 'E46C122C-F6D';
    const canvas = document.createElement('canvas');
    JsBarcode(canvas, barcodeValue, {
      format: 'CODE128',
      displayValue: false,
      width: 1.8,
      height: 40,
      margin: 0
    });
    const barcodeImage = canvas.toDataURL('image/png');
  
    const totalWeight = this.getTotalWeight(); // grams
    const totalProducts = this.getTotalProducts();
    const productsText = (commande.lignes || []).map(p => p.libelleProduit).join(', ') || 'N/A';
    const client = commande.client;
  
    const docDefinition = {
      pageSize: 'A6',
      pageMargins: [20, 15, 20, 15], // reduced top/bottom
      content: [
        { text: 'Ooredoo', style: 'logo' },
  
        { image: barcodeImage, width: 200, alignment: 'center', margin: [0, 6, 0, 3] },
        { text: barcodeValue, alignment: 'center', fontSize: 9, margin: [0, 0, 0, 6] },
  
        {
          columns: [
            { width: '*', stack: [{ text: 'Origin:', bold: true }, { text: 'Tunis' }] },
            { width: '*', stack: [{ text: 'Dest.:', bold: true }, { text: client.ville || '-' }] },
            { width: '*', stack: [{ text: 'Date:', bold: true }, { text: new Date().toLocaleDateString() }] }
          ],
          columnGap: 4,
          margin: [0, 0, 0, 4]
        },
  
        {
          columns: [
            { width: '*', stack: [{ text: 'Type:', bold: true }, { text: 'Standard' }] },
            { width: '*', stack: [{ text: 'AWB:', bold: true }, { text: barcodeValue }] }
          ],
          columnGap: 4,
          margin: [0, 0, 0, 4]
        },
  
        {
          text: `Weight: ${(totalWeight / 1000).toFixed(2)} KG\nPieces: ${totalProducts}`,
          style: 'section',
          margin: [0, 2, 0, 2]
        },
  
        { text: 'Products:', bold: true, margin: [0, 4, 0, 1] },
        { text: productsText, style: 'section' },
  
        {
          text: `${client.nom} ${client.prenom}\n${client.rue}, ${client.ville} ${client.codePostal}\n${client.tel}`,
          style: 'section',
          margin: [0, 5, 0, 0]
        },
  
        { text: 'Remarks: -', style: 'section', margin: [0, 5, 0, 0] },
  
         
          { text: 'Powered by Ooredoo', style: 'footer', margin: [0, 10, 0, 0] },
          { text: 'https://www.ooredoo.tn', style: 'footer' },
          { text: 'Tunis, Tunisia', style: 'footer' }
      ],
      styles: {
        logo: {
          fontSize: 18,
          bold: true,
          color: '#e74c3c',
          margin: [0, 0, 0, 5]
        },
        section: {
          fontSize: 8.5,
          lineHeight: 1.1
        },
        footer: {
          fontSize: 7,
          italics: true,
          color: '#999'
        }
      }
    };
  
    (pdfMake as any).createPdf(docDefinition).open();
  }
  
  
  getTotalWeight(): number {
    if (!this.commande?.lignes) return 0;
    return this.commande.lignes.reduce((sum, l) => sum + (l.poids || 0) * (l.quantite || 0), 0);
  }
  
  getTotalProducts(): number {
    if (!this.commande?.lignes) return 0;
    return this.commande.lignes.reduce((sum, l) => sum + (l.quantite || 0), 0);
  }
  
  
  
}
