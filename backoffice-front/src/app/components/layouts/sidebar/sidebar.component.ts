// Updated SidebarComponent with Competitive Analysis for Marketing

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, OnInit, Output } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

interface MenuItem {
  title: string;
  path: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  role: string | null = null;
  username: string | null = null;
  panelTitle = '';
  roleDisplayName = '';
  menuItems: MenuItem[] = [];
  isCollapsed = false;
  isMobile = false;

  @Output() collapseChange = new EventEmitter<boolean>();

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.role = this.authService.getUserRole();
    this.username = this.authService.getUsername();
    this.setupSidebarForRole();
    this.checkScreenSize();
    this.isCollapsed = this.isMobile;
  }

  toggleSidebar(): void {
    this.isCollapsed = !this.isCollapsed;
    this.collapseChange.emit(this.isCollapsed);
  }

  @HostListener('window:resize')
  onResize(): void {
    const wasMobile = this.isMobile;
    this.checkScreenSize();

    if (!wasMobile && this.isMobile) {
      this.isCollapsed = true;
    } else if (wasMobile && !this.isMobile) {
      this.isCollapsed = false;
    }
  }

  private checkScreenSize(): void {
    this.isMobile = window.innerWidth < 768;
  }

  private setupSidebarForRole(): void {
    switch (this.role) {
      case 'ADMINISTRATOR':
        this.panelTitle = 'Administration Panel';
        this.roleDisplayName = 'Administrator';
        this.menuItems = [
          { title: 'Dashboard', path: '/dashboard/admin', icon: 'fas fa-th' },
          { title: 'Users', path: '/dashboard/admin/users', icon: 'fas fa-users' }
        ];
        break;
      case 'MARKETING':
        this.panelTitle = 'Marketing Panel';
        this.roleDisplayName = 'Marketing';
        this.menuItems = [
          { title: 'Dashboard', path: '/dashboard/marketing', icon: 'fas fa-chart-line' },
          { title: 'Products', path: '/dashboard/marketing/produits', icon: 'fas fa-box-open' },
          { title: 'Categories', path: '/dashboard/marketing/gammes', icon: 'fas fa-layer-group' },
          { title: 'Brands', path: '/dashboard/marketing/marques', icon: 'fas fa-copyright' },
          { title: 'Packs', path: '/dashboard/marketing/packs', icon: 'fas fa-gift' },
          { title: 'Clients', path: '/dashboard/marketing/clients', icon: 'fas fa-users' },
          { title: 'Orders', path: '/dashboard/marketing/commandes', icon: 'fas fa-shopping-cart' },
          { title: 'Competitive Analysis', path: '/dashboard/marketing/competitive-analysis', icon: 'fas fa-lightbulb' }
        ];
        break;
      case 'LOGISTIQUE':
        this.panelTitle = 'Logistic Panel';
        this.roleDisplayName = 'Logistique';
        this.menuItems = [
          { title: 'Dashboard', path: '/dashboard/logistique', icon: 'fas fa-chart-line' },
          { title: 'Products', path: '/dashboard/logistique/produits', icon: 'fas fa-box-open' },
          { title: 'Deliveries', path: '/dashboard/logistique/livraisons', icon: 'fas fa-shipping-fast' }
        ];
        break;
    }
  }
}
