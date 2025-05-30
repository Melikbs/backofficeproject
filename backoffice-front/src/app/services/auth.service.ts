import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
interface JwtPayload {
  sub: string; // or `username` depending on your backend, adjust accordingly
  role?: string;
}
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private baseUrl = 'http://localhost:8080'; 
  private _cachedRole: string | null = null;

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.baseUrl}/login`, credentials).pipe(
      tap(response => {
        const token = response?.token?.trim();
  
        if (!token || token.split('.').length !== 3) {
          throw new Error('Token JWT invalide');
        }
  
        localStorage.setItem('authToken', token);
  
        // ✅ Decode the token to extract username and role
        const decoded = jwtDecode<JwtPayload>(token);
        const username = decoded.sub; // or decoded.username if your backend uses that
        const role = decoded.role;
  
        localStorage.setItem('username', username);
        if (role) localStorage.setItem('role', role);
  
        this._cachedRole = null;
        this.redirectUser();
      }),
      catchError(error => {
        localStorage.removeItem('authToken');
        return throwError(() => error);
      })
    );
  }

  checkUsernameAvailable(username: string): Observable<{ available: boolean }> {
    return this.http.post<{ available: boolean }>(`${this.baseUrl}/check-username`, { username }).pipe(
      catchError(() => throwError(() => ({ available: false })))
    );
  }

  checkEmailAvailable(email: string): Observable<{ available: boolean }> {
    return this.http.post<{ available: boolean }>(`${this.baseUrl}/check-email`, { email }).pipe(
      catchError(() => throwError(() => ({ available: false })))
    );
  }

  parseBackendErrors(error: any): { [key: string]: string } {
    const errors: { [key: string]: string } = {};

    if (error.errors) {
      error.errors.forEach((err: any) => {
        if (err.field === 'username') errors['username'] = err.defaultMessage;
        if (err.field === 'email') errors['email'] = err.defaultMessage;
        if (err.field === 'password') errors['password'] = err.defaultMessage;
      });
    } else if (error.message) {
      if (error.message.includes('Email')) errors['email'] = error.message;
      if (error.message.includes('Username')) errors['username'] = error.message;
      if (error.message.includes('Password')) errors['password'] = error.message;
    }

    return errors;
  }

  register(user: { username: string, email: string, password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, user).pipe(
      catchError(error => {
        if (error.status === 400) {
          const backendErrors = this.parseBackendErrors(error.error);
          return throwError(() => ({ validationErrors: backendErrors }));
        }
        return throwError(() => ({
          message: error.error?.message || 'Registration failed. Please try again.'
        }));
      })
    );
  }

  saveToken(token: string): void {
    console.log("Saving Token:", token);
    localStorage.setItem('authToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  getUserRole(): string | null {
    if (!this._cachedRole) {
      const token = this.getToken();
      try {
        if (token) {
          const decoded = jwtDecode<{ role?: string }>(token);
          this._cachedRole = decoded.role?.replace('ROLE_', '').toUpperCase() || null;
        }
      } catch (e) {
        console.error('Échec décodage:', e);
      }
    }
    return this._cachedRole;
  }

  clearRoleCache(): void {
    this._cachedRole = null;
  }

  private clearToken(): void {
    localStorage.removeItem('authToken');
  }

  redirectUser(): void {
    const role = this.getUserRole();

    if (!role) {
      this.router.navigate(['/login']);
      return;
    }

    switch (role) {
      case 'ADMINISTRATOR':
        this.router.navigate(['/dashboard/admin']);
        break;
      case 'MARKETING':
        this.router.navigate(['/dashboard/marketing']);
        break;
      case 'LOGISTIQUE':
        this.router.navigate(['/dashboard/logistique']);
        break;
      default:
        this.clearToken();
        this.router.navigate(['/login']);
    }
  }

  hasRole(role: string): boolean {
    const token = this.getToken();
    try {
      if (!token) {
        throw new Error('Token is null');
      }
      const decoded = jwtDecode<{ role?: string | string[] }>(token);

      console.log('Token décodé pour hasRole:', decoded);

      const roles = decoded.role;

      if (Array.isArray(roles)) {
        return roles.includes(role.toUpperCase());
      } else if (typeof roles === 'string') {
        return roles.toUpperCase() === role.toUpperCase();
      }

      return false;
    } catch (e) {
      console.error('Erreur dans hasRole:', e);
      return false;
    }
  }

  logout(): void {
    this.clearToken();
    this._cachedRole = null;
    window.history.replaceState(null, '', '/login?logoutSuccess=true');
    this.router.navigate(['/login']);
  }

  getUsername(): string | null {
    return localStorage.getItem('username'); // or wherever you store it
  }
  

}
