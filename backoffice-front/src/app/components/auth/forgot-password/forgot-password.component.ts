// forgot-password.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from 'src/app/navbar/navbar.component';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule, NavbarComponent,FormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  loading = false;
  email: string = '';
  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (!this.email && this.forgotForm.invalid) {
      this.showValidationError();
      return;

    }

    this.loading = true;
    Swal.fire({
      title: 'Sending reset link...',
      didOpen: () => {
        Swal.showLoading();
      },
      allowOutsideClick: false,
      allowEscapeKey: false,
      showConfirmButton: false,
      customClass: {
        popup: 'custom-swal-popup loading-swal'
      }
    });
    this.http.post('http://localhost:8080/users/forgot-password', {email: this.email },this.forgotForm.value)
      .subscribe({
        next: () => this.handleSuccess(),
        error: (error) => this.handleError(error)
      });}
      private handleSuccess() {
        this.loading = false;
        Swal.close();
     
    Swal.fire({
      html: `
        <div class="custom-swal-success">
          <div class="swal-icon-box">
            <i class="fas fa-paper-plane"></i>
          </div>
          <h3>Reset Link Sent</h3>
          <p>We've sent instructions to your email</p>
        </div>
      `,
      showConfirmButton: false,
      timer: 3000,
      customClass: {
        popup: 'custom-swal-popup success-swal'
      }
    });
        this.router.navigate(['/login']);
      
    }

  private handleError(error: any) {
    this.loading = false;
    Swal.close();
    const message = error.error?.message || 'An unknown error occurred';
    
    Swal.fire({
      html: `
        <div class="custom-swal-error">
          <div class="swal-icon-box">
            <i class="fas fa-exclamation-triangle"></i>
          </div>
          <h3>Request Failed</h3>
          <p>${message}</p>
        </div>
      `,
      confirmButtonText: 'Try Again',
      customClass: {
        popup: 'custom-swal-popup error-swal',
        confirmButton: 'custom-swal-btn'
      },
      buttonsStyling: false
    });
  }

  private showValidationError() {
    Swal.fire({
      html: `
        <div class="custom-swal-error">
          <div class="swal-icon-box">
            <i class="fas fa-exclamation-circle"></i>
          </div>
          <h3>Invalid Email</h3>
          <p>Please enter a valid email address</p>
        </div>
      `,
      confirmButtonText: 'OK',
      customClass: {
        popup: 'custom-swal-popup error-swal',
        confirmButton: 'custom-swal-btn'
      },
      buttonsStyling: false
    });
  }
}