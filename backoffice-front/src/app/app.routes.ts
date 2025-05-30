import { Routes } from '@angular/router';
import { LoginComponent } from './components/auth/login/login.component';
import { ForgotPasswordComponent } from './components/auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/auth/reset-password/reset-password.component';
import { AuthGuard } from './guards/auth.guard';
import { NoAuthGuard } from './guards/no-auth-guard.guard'; // <- Add this line
import { GammeListComponent } from './marketing/gammes/gamme-list.component';
import { GammeFormComponent } from './marketing/gammes/gamme-form.component';
import { LogistiqueGuard } from './guards/logistique.guard';
import { MarketingGuard } from './guards/marketing.guard';
import { StockManagementComponent } from './produit/stockmanagement/stock-management.component';
import { CompetitiveAnalysisComponent } from './marketing/analyseAI/competitive-analysis.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [NoAuthGuard] },
  {
    path: 'register',
    loadComponent: () =>
      import('./components/auth/register/register.component').then(m => m.RegisterComponent),
    canActivate: [NoAuthGuard],
  },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  {
    path: 'dashboard',
    loadComponent: () =>
      import('./components/layouts/main-layout.component').then(m => m.MainLayoutComponent),
    canActivate: [AuthGuard],
    children: [
      // Admin section
      {
        path: 'admin',
        canActivate: [AuthGuard],
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
          },
          {
            path: 'users',
            loadComponent: () =>
              import(
                './components/layouts/sidebar/Users/users-list/users-list.component'
              ).then(m => m.UsersListComponent),
          },
          // … any other admin routes …
        ],
      },

      // Marketing section
      {
        path: 'marketing',
        canActivate: [MarketingGuard],
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
          },
           {
      path: 'competitive-analysis',
      loadComponent: () =>
        import('./marketing/analyseAI/competitive-analysis.component').then(
          m => m.CompetitiveAnalysisComponent
        )
    },
          {
            path: 'marques',
            loadComponent: () =>
              import('./marketing/marque-list/marque-list.component').then(
                m => m.MarqueListComponent
              ),
          },
          {
            path: 'gammes',
            component: GammeListComponent,
          },
          {
            path: 'produits',
            loadComponent: () =>
              import('./produit/produit-management.component').then(
                m => m.ProduitManagementComponent
              ),
          },
          {
            path: 'produits/new',
            loadComponent: () =>
              import('./produit/produit-management.component').then(
                m => m.ProduitManagementComponent
              ),
          },
          {
            path: 'produits/:id',
            loadComponent: () =>
              import('./produit/produit-management.component').then(
                m => m.ProduitManagementComponent
              ),
          },
          {
            path: 'packs',
            children: [
              {
                path: 'create',
                loadComponent: () =>
                  import('./packs/pack-creation/pack-creation.component').then(
                    m => m.PackCreationComponent
                  ),
              },
              {
                path: 'edit/:id',
                loadComponent: () =>
                  import('./packs/pack-creation/pack-creation.component').then(
                    m => m.PackCreationComponent
                  ),
              },
              {
                path: '',
                loadComponent: () =>
                  import('./packs/pack-management.component').then(
                    m => m.PackManagementComponent
                  ),
              },
            ],
          },
          {
            path: 'clients',
            loadComponent: () =>
              import('./marketing/clients/client-list.component').then(
                m => m.ClientListComponent
              ),
          },
          {
            path: 'commandes',
            children: [
              {
                path: '',
                loadComponent: () =>
                  import('./marketing/commandes/commande-list.component').then(
                    m => m.CommandeListComponent
                  ),
              },
   
             

              {
                path: ':id',
                loadComponent: () =>
                  import('./marketing/commandes/commande-detail.component').then(
                    m => m.CommandeDetailComponent
                  ),
              },
            ],
          },
        ],
      },

      // Logistique section
      {
        path: 'logistique',
        canActivate: [LogistiqueGuard],
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
          },
          {
            path: 'produits',
            loadComponent: () =>
              import('./produit/produit-management.component').then(
                m => m.ProduitManagementComponent
              ),
          },
          {
            path: 'produits/:codeProduit/stocks',
            loadComponent: () =>
              import('./produit/stockmanagement/stock-management.component').then(
                m => m.StockManagementComponent
              ),
          },
          {
            path: 'logistique/stock-management/:codeProduit',
            loadComponent: () =>
              import('./produit/stockmanagement/stock-management.component').then(
                m => m.StockManagementComponent
              ),
          },
          {
            path: 'livraisons',
            loadComponent: () =>
              import('./logistique/livraison/livraison-list.component').then(
                m => m.LivraisonListComponent
              ),
          },
          {
            path: 'livraisons/:id',
            loadComponent: () =>
              import('./logistique/livraison/livraison-detail.component').then(
                m => m.LivraisonDetailComponent
              ),
          }
        ],
      },
      
    ],
  },

  // Default & fallback
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];