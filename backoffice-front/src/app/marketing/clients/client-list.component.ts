import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClientService } from '../../services/client.service';
import Swal from 'sweetalert2';
import { ClientDTO } from '../../models/client-dto.model';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SwalService } from 'src/app/services/sweetalert.service';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss']
})
export class ClientListComponent implements OnInit {
  clients: ClientDTO[] = [];

  constructor(private clientService: ClientService,private swalService: SwalService) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.clientService.getClients().subscribe(data => {
      this.clients = data;
    });
  }

  blacklistClient(codeClient: number): void {
    this.swalService.fire({
        title: 'Are you sure?',
        text: "You will blacklist this client!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#e74c3c',
        confirmButtonText: 'Yes, blacklist it!'
      }).then((result) => {
        if (result.isConfirmed) {
          this.clientService.deactivateClient(codeClient).subscribe(() => {
            this.swalService.toast('The client has been blacklisted.');
            const client = this.clients.find(c => c.codeClient === codeClient);
            if (client) client.actif = false;
          });
        }
      });
      
  }

  reactivateClient(codeClient: number): void {
    Swal.fire({
      title: 'Are you sure?',
      text: "You will reactivate this client!",
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#2ecc71',
      confirmButtonText: 'Yes, reactivate it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clientService.reactivateClient(codeClient).subscribe(() => {
          Swal.fire('Reactivated!', 'The client is now active.', 'success');
          const client = this.clients.find(c => c.codeClient === codeClient);
          if (client) client.actif = true;
        });
      }
    });
  }
}
