import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule,CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  pageTitle = '';
  isLoginPage = true;

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.updatePageState();
    });
    
    // Initial check
    this.updatePageState();
  }

  private updatePageState(): void {
    const currentRoute = this.router.url;
    this.isLoginPage = currentRoute === '/login';
    this.pageTitle = this.isLoginPage ? 'Login' : 'Register';
  }
}