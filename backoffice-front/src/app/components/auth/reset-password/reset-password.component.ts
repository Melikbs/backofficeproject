import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from 'src/app/navbar/navbar.component';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, CommonModule, NavbarComponent],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  newPassword: string = '';
  confirmPassword: string = '';
  token: string = '';
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.token = this.route.snapshot.queryParams['token'];
  }

  onSubmit() {
    if (this.newPassword !== this.confirmPassword) {
      this.showError('Passwords do not match');
      return;
    }

    if (!this.validatePassword(this.newPassword)) {
      return;
    }

    this.loading = true;

    this.http.post('http://localhost:8080/users/reset-password', {
      token: this.token,
      newPassword: this.newPassword
    }).subscribe({
      next: () => this.handleSuccess(),
      error: (error) => this.handleError(error)
    });
  }

  private handleSuccess() {
    this.loading = false;

    Swal.fire({
      html: `
        <div class="custom-swal-success">
          <div class="swal-icon-box">
            <i class="fas fa-check-circle"></i>
          </div>
          <h3>Password Reset</h3>
          <p>Your password has been successfully updated.</p>
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
    const message = error.error?.message || 'Failed to reset password. Please try again.';

    this.showError(message);
  }

  private validatePassword(password: string): boolean {
    const requirements = [
      { regex: /.{8,}/, message: 'At least 8 characters' },
      { regex: /[0-9]/, message: 'Must contain a number' },
      { regex: /[a-z]/, message: 'Must contain lowercase letter' },
      { regex: /[A-Z]/, message: 'Must contain uppercase letter' },
      { regex: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/, message: 'Must contain special character' }
    ];

    const failed = requirements.find(r => !r.regex.test(password));
    if (failed) {
      this.showError(`Password requirements: ${failed.message}`);
      return false;
    }

    return true;
  }

  private showError(message: string) {
    Swal.fire({
      html: `
        <div class="custom-swal-error">
          <div class="swal-icon-box">
            <i class="fas fa-exclamation-circle"></i>
          </div>
          <h3>Error</h3>
          <p>${message}</p>
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
