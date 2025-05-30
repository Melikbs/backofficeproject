import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { CommandeResponse } from "src/app/models/commande-response.model";
import { CommandeService } from "src/app/services/commande.service";
import Swal from "sweetalert2";

@Component({
    selector: 'app-commande-list',
    standalone: true,
    imports: [CommonModule,FormsModule,ReactiveFormsModule,RouterModule],
    templateUrl: './commande-list.component.html',
    styleUrls: ['./commande-list.component.scss'],
  })
  export class CommandeListComponent implements OnInit {
    commandes: CommandeResponse[] = [];
  
    constructor(private commandeService: CommandeService, private router: Router) {}
  
    ngOnInit(): void {
      this.commandeService.getAll().subscribe(data => this.commandes = data);
    }
  
    goToDetails(id: number): void {
      this.router.navigate(['/dashboard/marketing/commandes', id]);
    }
    
  
    validateCommande(id: number): void {
        this.commandeService.validate(id).subscribe(() => {
          const cmd = this.commandes.find(c => c.codeCommande === id);
          if (cmd) cmd.status = 'VALIDATED';
      
          Swal.fire({
            icon: 'success',
            title: '✅ Order Validated',
            text: `Order #${id} has been successfully marked as validated.`,
            confirmButtonColor: '#e74c3c',
            background: '#f8f9fa',
            color: '#343a40'
          });
        });
      }
      refuseCommande(id: number): void {
        this.commandeService.refuse(id).subscribe(() => {
          const cmd = this.commandes.find(c => c.codeCommande === id);
          if (cmd) cmd.status = 'REFUSED';
      
          Swal.fire({
            icon: 'info',
            title: 'Order Refused ❌',
            text: `Order #${id} has been marked as refused.`,
            confirmButtonColor: '#e74c3c',
            background: '#f8f9fa',
            color: '#343a40'
          });
        });
      }
      getStatusLabel(status: string): string {
        switch (status) {
          case 'VALIDATED': return 'ACTIVE';
          case 'REFUSED': return 'REFUSED';
          case 'PENDING': return 'PENDING';
          default: return '-';
        }
      }
      
  }
  