import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { CommandeDetail } from "src/app/models/commande-detail.model";
import { CommandeService } from "src/app/services/commande.service";
import Swal from "sweetalert2";
import jsPDF from 'jspdf';
import { Livraison } from "src/app/models/livraison.model";

@Component({
    selector: 'app-commande-detail',
    standalone: true,
    imports: [FormsModule,CommonModule,ReactiveFormsModule,RouterModule],
    templateUrl: './commande-detail.component.html',
    styleUrls: ['./commande-detail.component.scss'],
  })
  export class CommandeDetailComponent implements OnInit {
    commande?: CommandeDetail;
    livraison?: Livraison;

    constructor(private route: ActivatedRoute, private service: CommandeService, private router: Router) {}
  
    ngOnInit(): void {
      const id = +this.route.snapshot.paramMap.get('id')!;
      this.service.getDetails(id).subscribe(data => {
        this.commande = data;
    
        // si la commande est validée, on charge la livraison liée
        if (this.commande.status === 'VALIDATED') {
          this.service.getLivraisonByCommande(this.commande.codeCommande).subscribe({
            next: data => this.livraison = data,
            error: () => this.livraison = undefined // en cas d'absence
          });
        }
      });
    }
    
  
    validate(): void {
        if (this.commande) {
          this.service.validate(this.commande.codeCommande).subscribe(() => {
            this.commande!.status = 'VALIDATED';
            this.showToast('Commande validée ✅', 'success');
            Swal.fire({
              icon: 'success',
              title: 'Commande validée ✅',
              text: `La commande #${this.commande?.codeCommande} a été validée.`,
              confirmButtonColor: '#e74c3c',
              background: '#f8f9fa',
              color: '#343a40',
              showClass: {
                popup: 'animate__animated animate__fadeInDown'
              },
              hideClass: {
                popup: 'animate__animated animate__fadeOutUp'
              }
            });
          });
        }
      }
      refuse(): void {
        if (this.commande) {
          this.service.refuse(this.commande.codeCommande).subscribe(() => {
            this.commande!.status = 'REFUSED';
            this.showToast('Commande refusée ❌', 'info');
            Swal.fire({
              icon: 'info',
              title: 'Commande refusée ❌',
              text: `La commande #${this.commande?.codeCommande} a été refusée.`,
              confirmButtonColor: '#e74c3c',
              background: '#f8f9fa',
              color: '#343a40',
              showClass: {
                popup: 'animate__animated animate__fadeInDown'
              },
              hideClass: {
                popup: 'animate__animated animate__fadeOutUp'
              }
            });
          });
        }
      }
      showToast(message: string, icon: 'success' | 'info') {
        Swal.fire({
          toast: true,
          position: 'top-end',
          icon: icon,
          title: message,
          showConfirmButton: false,
          timer: 2500,
          timerProgressBar: true,
          background: '#f8f9fa',
          color: '#343a40',
          didOpen: toast => {
            toast.addEventListener('mouseenter', Swal.stopTimer);
            toast.addEventListener('mouseleave', Swal.resumeTimer);
          },
          showClass: {
            popup: 'animate__animated animate__fadeInRight'
          },
          hideClass: {
            popup: 'animate__animated animate__fadeOutRight'
          }
        });
      }
      
    back(): void {
      this.router.navigate(['/dashboard/marketing/commandes']);
    }
   

    generatePDF(): void {
      if (!this.commande) return;
    
      const doc = new jsPDF();
    
      // 🟥 Logo
      const logoPath = '../../../assets/logo_ooredoo.png'; // adapt if PNG = add `.png`
      doc.addImage(logoPath, 'PNG', 10, 10, 40, 15);
    
      // 🧾 Title + Invoice Metadata
      doc.setFontSize(18);
      doc.setTextColor('#e74c3c');
      doc.text('Facture', 150, 20, { align: 'right' });
    
      doc.setFontSize(11);
      doc.setTextColor('#343a40');
      doc.text(`Facture N°: ${this.commande.codeCommande}`, 150, 30, { align: 'right' });
      doc.text(`Date: ${this.commande.dateCommande}`, 150, 36, { align: 'right' });
      doc.text(`Statut: ${this.commande.status}`, 150, 42, { align: 'right' });
    
      // 👤 Client Info
      let y = 60;
      doc.setFontSize(12);
      doc.text('Informations client', 10, y);
      doc.setFontSize(10);
      y += 6;
      doc.text(`Nom complet: ${this.commande.client.nom} ${this.commande.client.prenom}`, 10, y);
      y += 6;
      doc.text(`CIN: ${this.commande.client.cin}`, 10, y);
      y += 6;
      doc.text(`Téléphone: ${this.commande.client.tel}`, 10, y);
      y += 6;
      doc.text(`Email: ${this.commande.client.email}`, 10, y);
      y += 6;
      doc.text(`Adresse: ${this.commande.client.rue}, ${this.commande.client.ville} ${this.commande.client.codePostal}`, 10, y);
    
      // 📦 Product Table Header
      y += 14;
      doc.setFontSize(12);
      doc.text('Produits commandés', 10, y);
      y += 6;
      doc.setFontSize(10);
      doc.setTextColor(255, 255, 255);
      doc.setFillColor(231, 76, 60);
      doc.rect(10, y, 190, 8, 'F');
      doc.text('Produit', 12, y + 6);
      doc.text('Quantité', 85, y + 6);
      doc.text('Prix Unitaire', 115, y + 6);
      doc.text('Total', 160, y + 6);
    
      // 🧾 Table Rows
      y += 10;
      doc.setTextColor(52, 58, 64); // back to gray-dark
    
      this.commande.lignes.forEach((p, i) => {
        const total = (p.prix * p.quantite).toFixed(2);
        doc.text(`${p.libelleProduit}`, 12, y);
        doc.text(`${p.quantite}`, 90, y);
        doc.text(`${p.prix.toFixed(2)} DT`, 120, y);
        doc.text(`${total} DT`, 160, y);
        y += 7;
      });
    
      // 📊 Totals
      y += 10;
      doc.setFontSize(11);
      doc.text(`Sous-total: ${this.commande.sousTotal.toFixed(2)} DT`, 130, y);
      y += 6;
      doc.text(`TVA (17%): ${this.commande.tva.toFixed(2)} DT`, 130, y);
      y += 6;
      doc.setTextColor('#e74c3c');
      doc.setFontSize(12);
      doc.text(`TOTAL: ${this.commande.total.toFixed(2)} DT`, 130, y);
    
      // 📝 Footer
      y += 20;
      doc.setFontSize(10);
      doc.setTextColor('#343a40');
      doc.text('Merci pour votre confiance.', 10, y);
      y += 5;
      doc.setFontSize(9);
      doc.text('Cette facture est générée automatiquement par le système Ooredoo.', 10, y);
    
      // 💾 Save PDF
      doc.save(`Ooredoo_Facture_Commande_${this.commande.codeCommande}.pdf`);
    }
  }    