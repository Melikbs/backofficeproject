import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { trigger, state, style, animate, transition } from '@angular/animations';
import Swal from 'sweetalert2';
import { AuthService } from 'src/app/services/auth.service';

import { NavbarComponent } from '../../../navbar/navbar.component';
const SWAL_CONFIG = {
  success: {
    iconColor: '#28a745',
    gradient: 'linear-gradient(135deg, #28a745 0%, #218838 100%)',
    icon: 'fas fa-check-circle',
    animation: 'swal2-animate-success-icon'
  },
  error: {
    iconColor: '#dc3545',
    gradient: 'linear-gradient(135deg, #dc3545 0%, #c82333 100%)',
    icon: 'fas fa-exclamation-triangle',
    animation: 'swal2-animate-error-icon'
  },
  warning: {
    iconColor: '#ffc107',
    gradient: 'linear-gradient(135deg, #ffc107 0%, #e0a800 100%)',
    icon: 'fas fa-envelope-clock',
    animation: 'swal2-animate-warning-icon'
  }
};
@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [ReactiveFormsModule, CommonModule, NavbarComponent]}
)
 
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  showPassword = false;
  showResetRequested = false;
  showResetSuccess = false;
  formSubmitted = false;
  private showAlert(config: {
    title: string,
    text: string,
    icon: 'warning' | 'error' | 'success' | 'info',
    showCancelButton?: boolean,
    confirmButtonText?: string,
    cancelButtonText?: string,
    action?: () => void
  }) {
    Swal.fire({
      title: config.title,
      text: config.text,
      icon: config.icon,
      showCancelButton: config.showCancelButton || false,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: config.confirmButtonText || 'OK',
      cancelButtonText: config.cancelButtonText || 'Annuler',
      customClass: {
        container: 'custom-swal-container',
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        htmlContainer: 'custom-swal-text',
        confirmButton: 'custom-swal-confirm-btn',
        cancelButton: 'custom-swal-cancel-btn'
      }
    }).then((result) => {
      if (result.isConfirmed && config.action) {
        config.action();
      }
    });}
  private getAlertHtml(message: string, icon: string): string {
    return `
      <div class="swal-content">
        <div class="swal-icon-wrapper">
          <i class="${icon} swal-main-icon"></i>
        </div>
        <div class="swal-message">${message}</div>
      </div>
    `;
  }
  private animateIcon(animationClass: string) {
    const icon = document.querySelector('.swal-main-icon');
    icon?.classList.add(animationClass);
  }


  constructor(
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
  
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['logoutSuccess']) {
        Swal.fire({
          icon: 'success',
          title: 'Déconnecté',
          text: 'Vous avez été déconnecté avec succès.',
          timer: 1500,
          showConfirmButton: false
        });
      }
    });
  };
  
  onSubmit(): void {
    this.formSubmitted = true;
  
    if (this.loginForm.invalid) {
      this.showValidationErrors();
      return;
    }
  
    this.loading = true;
  
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.showSuccessAlert('Connexion réussie !');
        this.authService.redirectUser(); // ✅ Redirect based on real role
      },
      error: (error) => {
        this.loading = false;
        if (error?.error?.errorType === 'ACCOUNT_NOT_ACTIVATED' || 
            error?.error?.message?.toLowerCase().includes('activation')) {
          this.showActivationAlert();
        } else {
          this.showErrorAlert('Invalid email or password');
        }
        this.cdr.detectChanges();
        console.error('Login error:', error);
      }
    });
  }
  

  
private showValidationErrors(): void {
  const errors = [];
  
  if (this.loginForm.get('email')?.errors) {
    if (this.loginForm.get('email')?.errors?.['required']) {
      errors.push('Email requis');
    }
    if (this.loginForm.get('email')?.errors?.['email']) {
      errors.push('Format email invalide');
    }
  }

  if (this.loginForm.get('password')?.errors?.['required']) {
    errors.push('Mot de passe requis');
  }

  this.showErrorAlert(errors);
}

private showErrorAlert(messages: string | string[]): void {
  const message = Array.isArray(messages) 
    ? messages.join('<br>')
    : messages;

  this.showAlert({
    title: 'Error',
    text: message,
    icon: 'error',
    confirmButtonText: 'Got It'
  });
}
  private showActivationAlert(): void {
    this.showAlert({
      title: 'Require Activation',
      text: "wait for your activating your account !",
      icon: 'warning'
    });
  }
  private showSuccessAlert(message: string, redirectUrl?: string): void {
    this.showAlert({
      title: 'Success',
      text: message,
      icon: 'success',
      action: () => {
        if(redirectUrl) {
          this.router.navigate([redirectUrl]);
        }
      }
    });
  
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  goToForgotPassword(): void {
    this.router.navigate(['/forgot-password']);
  }
}
