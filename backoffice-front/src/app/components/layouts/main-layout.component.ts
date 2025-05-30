import { ChangeDetectorRef, Component, HostListener, NgZone, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarMainLayoutComponent } from './navbar/navbar-main-layout.component';
import { SidebarComponent } from '../layouts/sidebar/sidebar.component';
import { NavbarComponent } from '../../navbar/navbar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarMainLayoutComponent, SidebarComponent, NavbarComponent],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit {
  currentPageTitle = 'Dashboard';
  isSidebarCollapsed = false;
  isMobile = false;

  // ✅ Ajout de la variable pour le backdrop
  isModalOpen = false;

  constructor(private cdRef: ChangeDetectorRef, private zone: NgZone) {}

  ngOnInit(): void {
    this.checkScreenSize();
    this.observeModalState(); // ✅ Appel de l’observateur
  }

  @HostListener('window:resize')
  onResize() {
    const wasMobile = this.isMobile;
    this.checkScreenSize();

    if (wasMobile && !this.isMobile) {
      this.isSidebarCollapsed = false;

      this.zone.runOutsideAngular(() => {
        setTimeout(() => {
          window.dispatchEvent(new Event('resize'));
        }, 100);
      });

      this.cdRef.detectChanges();
    }
  }

  checkScreenSize() {
    this.isMobile = window.innerWidth < 768;
    if (this.isMobile) {
      this.isSidebarCollapsed = true;
    }
  }

  onSidebarToggle(collapsed: boolean) {
    this.isSidebarCollapsed = collapsed;
  }

  updateTitle(component: any) {
    if (component.constructor?.ɵcmp?.data?.title) {
      this.currentPageTitle = component.constructor.ɵcmp.data.title;
    }
  }

  // ✅ Méthode pour observer l’état des modals Bootstrap
  observeModalState() {
    const observer = new MutationObserver(() => {
      this.isModalOpen = document.body.classList.contains('modal-open');
    });
    observer.observe(document.body, { attributes: true, attributeFilter: ['class'] });
  }
}
