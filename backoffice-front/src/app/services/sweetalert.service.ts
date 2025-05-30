import { Injectable } from '@angular/core';
import Swal, { SweetAlertOptions, SweetAlertResult } from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class SwalService {
  fire(options: SweetAlertOptions): Promise<SweetAlertResult<any>> {
    return Swal.fire({
      ...options,
      didOpen: () => document.body.style.overflow = 'hidden',
      willClose: () => document.body.style.overflow = 'auto'
    });
  }

  toast(message: string, icon: 'success' | 'error' | 'info' | 'warning' = 'success') {
    return Swal.fire({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      icon,
      title: message
    });
  }
}
