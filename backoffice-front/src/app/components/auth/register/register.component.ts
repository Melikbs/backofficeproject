import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { NavbarComponent } from "../../../navbar/navbar.component";
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [NavbarComponent, ReactiveFormsModule, CommonModule]
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20),
        Validators.pattern(/^[a-zA-Z0-9_]+$/)
      ]],
      email: ['', [
        Validators.required,
        Validators.email
      ]],
      password: ['', [
        Validators.required,
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/)
      ]]
    });
  }

  ngOnInit(): void {
    this.setupAvailabilityCheck('username');
    this.setupAvailabilityCheck('email');
  }

  private setupAvailabilityCheck(field: 'username' | 'email'): void {
    const control = this.registerForm.get(field);
    control?.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(value => {
        if (control?.hasError(`${field}Taken`)) control.setErrors(null);
        return field === 'username'
          ? this.authService.checkUsernameAvailable(value)
          : this.authService.checkEmailAvailable(value);
      })
    ).subscribe({
      next: res => !res.available && control?.setErrors({ [`${field}Taken`]: true }),
      error: () => control?.setErrors({ serverError: true })
    });
  }

  get username() { return this.registerForm.get('username'); }
  get email() { return this.registerForm.get('email'); }
  get password() { return this.registerForm.get('password'); }

  register(): void {
    this.registerForm.markAllAsTouched();
    if (this.registerForm.invalid) return this.showValidationErrors();

    this.isLoading = true;
    this.authService.register(this.registerForm.value).subscribe({
      next: () => this.showSuccessAlert(),
      error: (err) => this.handleRegistrationError(err)
    });
  }

  private showValidationErrors(): void {
    const errors = [];
    
    if (this.username?.errors) {
      if (this.username.hasError('required')) errors.push('Username is required');
      if (this.username.hasError('minlength')) errors.push('Username must be at least 3 characters');
      if (this.username.hasError('maxlength')) errors.push('Username cannot exceed 20 characters');
      if (this.username.hasError('pattern')) errors.push('Username can only contain letters, numbers, and underscores');
      if (this.username.hasError('usernameTaken')) errors.push('Username is already taken');
    }

    if (this.email?.errors) {
      if (this.email.hasError('required')) errors.push('Email is required');
      if (this.email.hasError('email')) errors.push('Invalid email format');
      if (this.email.hasError('emailTaken')) errors.push('Email is already registered');
    }

    if (this.password?.errors) {
      if (this.password.hasError('required')) errors.push('Password is required');
      if (this.password.hasError('pattern')) errors.push('Password must contain: 8+ characters, uppercase, lowercase, number, and special character');
    }

    this.showErrorAlert(errors);
  }

  private showSuccessAlert(): void {
    Swal.fire({
      html: `
        <div class="custom-swal-success">
          <div class="swal-icon-box">
            <i class="fas fa-check-circle"></i>
          </div>
          <h3>Registration Successful!</h3>
          <p>Please wait for activating your email. We will inform you!</p>
        </div>
      `,
      confirmButtonText: 'OK',
      customClass: { 
        popup: 'custom-swal-popup success-swal',
        confirmButton: 'custom-swal-btn' 
      },
      buttonsStyling: false
    });
  }

  private showErrorAlert(errors: string[]): void {
    Swal.fire({
      html: `
        <div class="custom-swal-error">
          <div class="swal-icon-box">
            <i class="fas fa-times-circle"></i>
          </div>
          <h3>Validation Required</h3>
          <ul class="error-list">
            ${errors.map(error => `<li>${error}</li>`).join('')}
          </ul>
        </div>
      `,
      confirmButtonText: 'Got It',
      customClass: { popup: 'custom-swal-popup error-swal', confirmButton: 'custom-swal-btn' },
      buttonsStyling: false
    });
  }

  private handleRegistrationError(error: any): void {
    this.isLoading = false;
    const errors = error?.validationErrors 
      ? Object.values(error.validationErrors) 
      : [error.message || 'Registration failed. Please try again.'];
    this.showErrorAlert(errors);
  }
}