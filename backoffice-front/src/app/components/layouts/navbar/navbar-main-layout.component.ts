import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Component({
  selector: 'app-navbar-main-layout',
  standalone: true,
  templateUrl: './navbar-main-layout.component.html',
  styleUrls: ['./navbar-main-layout.component.scss']
})
export class NavbarMainLayoutComponent {
  @Output() toggleSidebar = new EventEmitter<void>();

  constructor(private router: Router) {}

  logout() {
    Swal.fire({
      title: 'Êtes-vous sûr ?',
      text: 'Vous allez être déconnecté.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Oui, déconnecter',
      cancelButtonText: 'Annuler'
    }).then((result: SweetAlertResult) => {
      if (result.isConfirmed) {
        localStorage.clear();
        this.router.navigate(['/login']);
        Swal.fire({
          icon: 'success',
          title: 'Déconnecté',
          text: 'Vous avez été déconnecté avec succès.',
          timer: 1500,
          showConfirmButton: false
        });
      }
    });
  }
}