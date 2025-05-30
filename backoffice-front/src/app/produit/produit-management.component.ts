import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProduitService } from 'src/app/services/produit.service';
import { ProduitResponse } from 'src/app/models/produitresponse.model';
import { Stock } from '../models/stock.model';
import { AuthService } from '../services/auth.service';
import { StockService } from '../services/stock.service';
import { HttpErrorResponse } from '@angular/common/http';
import Swal from 'sweetalert2';
import { GammeService } from '../services/gamme.service';
import { MarqueService } from '../services/marque.service';
@Component({
  selector: 'app-produit-management',
  standalone: true,
  templateUrl: './produit-management.component.html',
  styleUrls: ['./produit-management.component.scss'],
  imports: [CommonModule, RouterModule, ReactiveFormsModule,FormsModule],
  providers: [StockService,ProduitService,AuthService]
})
export class ProduitManagementComponent implements OnInit {
  originalProduits: ProduitResponse[] = [];
  produits: ProduitResponse[] = [];
  produitForm: FormGroup;
  currentFile: File | null = null;
  currentImage: string | null = null;
  isEditing = false;
  isMarketing: boolean = false;
  isLogistics: boolean = false;
  // Stock management properties
  showStockManagement = false;
  currentProduct: ProduitResponse | null = null;
  currentStocks: Stock[] = [];
  gammes: any[] = [];
marques: any[] = [];
selectedCategory: string = '';
selectedBrand: string = '';
minPrice: number | null = null;
maxPrice: number | null = null;
imageErrorMessage: string = '';

  constructor(
    private produitService: ProduitService,
    private stockService: StockService,
    private gammeService: GammeService,
    private marqueService: MarqueService,
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.produitForm = this.fb.group({
      codeProduit: [null],
      libelle: ['', Validators.required],
      description: ['', Validators.required],
      prix: ['', [Validators.required, Validators.min(0)]],
      poids: ['', [Validators.required, Validators.min(0)]],
      actif: [true],
      codeGamme: [null, Validators.required],   // 🔴 Add this
      codeMarque: [null, Validators.required]   // 🔴 And this
    });
    
  }

  ngOnInit(): void {
    this.loadProduits();
    this.checkRoles();
    if (this.isLogistics) {
      this.gammeService.getAllGammes().subscribe(data => {
        this.gammes = data.filter(g => g.flag); // ✅ only active categories
      });
  
      this.marqueService.getAllMarques().subscribe(data => {
        this.marques = data.filter(m => m.actif); // ✅ only active brands
      });
    }
    if (this.isLogistics || this.isMarketing) {
      this.loadGammesEtMarques();
    }
  }
  private checkRoles() {
    this.isMarketing = this.authService.getUserRole() === 'MARKETING';
    this.isLogistics = this.authService.getUserRole() === 'LOGISTIQUE';
  }
  loadGammesEtMarques(): void {
    this.gammeService.getAllGammes().subscribe(g => this.gammes = g);
    this.marqueService.getAllMarques().subscribe(m => this.marques = m);
  }
  
  

  loadProduits(): void {
    this.produitService.getAllProduits().subscribe(produits => {
      this.originalProduits = produits;
      this.produits = produits;
    });
  }
  applyFilters(): void {
    this.produits = this.originalProduits.filter(p => {
      const categoryMatch = !this.selectedCategory || p.codeGamme == +this.selectedCategory;
      const brandMatch = !this.selectedBrand || p.codeMarque == +this.selectedBrand;
      const minMatch = this.minPrice == null || p.prix >= this.minPrice;
      const maxMatch = this.maxPrice == null || p.prix <= this.maxPrice;
      return categoryMatch && brandMatch && minMatch && maxMatch;
    });
  }
  resetFilters(): void {
    this.selectedCategory = '';
    this.selectedBrand = '';
    this.minPrice = null;
    this.maxPrice = null;
    this.produits = [...this.originalProduits];
  }
  async startAddProduit(): Promise<void> {
    if (!this.isMarketing) return;
  
    const { value: confirmed } = await Swal.fire({
      title: 'Add Product',
      html: `
  <div class="swal-form">
    <input id="swal-libelle" class="swal2-input" placeholder="Name" />
    <input id="swal-prix" class="swal2-input" placeholder="Price" type="number" />
    <input id="swal-poids" class="swal2-input" placeholder="Weight" type="number" />
    <textarea id="swal-description" class="swal2-textarea" placeholder="Description"></textarea>

    ${this.isMarketing ? `
      <select id="swal-gamme" class="swal2-select">
        <option disabled selected value="">Select Category</option>
        ${this.gammes.map(g => `<option value="${g.codeGamme}">${g.libelle}</option>`).join('')}
      </select>

      <select id="swal-marque" class="swal2-select">
        <option disabled selected value="">Select Brand</option>
        ${this.marques.map(m => `<option value="${m.codeMarque}">${m.libelle}</option>`).join('')}
      </select>
    ` : ''}

    <div class="file-input-wrapper">
  <label for="swal-image" class="file-input-label">
    <i class="fas fa-upload upload-icon"></i> Upload Image
  </label>
  <input id="swal-image" type="file" style="display: none;" />

  <span id="file-name" class="file-name">No file chosen</span>
  <span id="image-error" class="text-danger small mt-1 d-block"></span>
</div>

    

    <div id="swal-preview-container" class="image-preview-container"></div>
  </div>
`,

      confirmButtonText: 'Save',
      cancelButtonText: 'Cancel',
      showCancelButton: true,
      focusConfirm: false,
    
     
    
      showClass: {
        popup: 'swal2-animate-top' // ✅ custom entry animation
      },
      hideClass: {
        popup: 'swal2-hide-custom' // ✅ custom exit animation
      },
      customClass: {
        popup: 'custom-swal-popup', // ✅ ONLY your custom styling class here
        confirmButton: 'primary-btn',
        cancelButton: 'secondary-btn'
      }
    ,// ✅ Keep this to disable SweetAlert2 default animation
  
      didOpen: () => {
        const fileInput = document.getElementById('swal-image') as HTMLInputElement;
        const fileNameDisplay = document.getElementById('file-name');
        const preview = document.getElementById('swal-preview-container');
  
        if (fileInput && fileNameDisplay) {
          fileInput.addEventListener('change', () => {
            const file = fileInput.files?.[0];
            fileNameDisplay.textContent = file?.name || 'No file chosen';
  
            if (file && preview) {
              const reader = new FileReader();
              reader.onload = () => {
                preview.innerHTML = `<img src="${reader.result}" class="image-preview" alt="Preview">`;
              };
              reader.readAsDataURL(file);
            }
          });
        }
      },
      preConfirm: () => {
        const libelle = (document.getElementById('swal-libelle') as HTMLInputElement).value.trim();
        const prix = parseFloat((document.getElementById('swal-prix') as HTMLInputElement).value);
        const poids = parseFloat((document.getElementById('swal-poids') as HTMLInputElement).value);
        const description = (document.getElementById('swal-description') as HTMLTextAreaElement).value.trim();
        const imageFileInput = document.getElementById('swal-image') as HTMLInputElement;
        const imageFile = (document.getElementById('swal-image') as HTMLInputElement).files?.[0];
        const imageError = document.getElementById('image-error');
      
        if (!imageFile) {
          if (imageError) imageError.textContent = 'Veuillez sélectionner une image.';
          return false; // <== Essentiel pour empêcher la fermeture
        }
      
        if (imageError) imageError.textContent = '';

        const codeGamme = this.isMarketing 
        ? parseInt((document.getElementById('swal-gamme') as HTMLSelectElement)?.value) 
        : null;
      
      const codeMarque = this.isMarketing 
        ? parseInt((document.getElementById('swal-marque') as HTMLSelectElement)?.value) 
        : null;
        
        if (!codeGamme || !codeMarque) {
          Swal.showValidationMessage('Please select a category and a brand.');
          return;
        }
        
        if (!libelle || isNaN(prix) || isNaN(poids) || !description) {
          Swal.showValidationMessage('Veuillez remplir tous les champs correctement.');
          return;
        }
  
        return { libelle, prix, poids, description, imageFile, codeGamme, codeMarque };
        
      }
    });
  
    if (confirmed) {
      this.produitForm.patchValue({
        libelle: confirmed.libelle,
        prix: confirmed.prix,
        poids: confirmed.poids,
        description: confirmed.description,
        codeGamme: confirmed.codeGamme,
        codeMarque: confirmed.codeMarque
      });
      
      
      this.currentFile = confirmed.imageFile || null;
      this.saveProduit();
    }
  }
  

  

  // Marketing functions
  saveProduit(): void {
    if (this.produitForm.invalid || (!this.isMarketing)) return; // ✅ Only Marketing allowed to save
  
  
    const formData = new FormData();
    const produitData = this.produitForm.value;
    formData.append('codeGamme', String(produitData.codeGamme));
    formData.append('codeMarque', String(produitData.codeMarque));


    formData.append('libelle', String(produitData.libelle));
    formData.append('description', String(produitData.description));
    formData.append('prix', String(produitData.prix));
    formData.append('poids', String(produitData.poids));
    formData.append('actif', String(produitData.actif));
    if (this.isMarketing) {
      formData.append('codeGamme', String(produitData.codeGamme));
      formData.append('codeMarque', String(produitData.codeMarque));
    }
  
    if (this.currentFile && this.currentFile.name !== 'undefined') {
      const fileType = this.currentFile.type.split('/')[0];
      const fileSize = this.currentFile.size;
  
      if (fileType === 'image' && fileSize <= 5_000_000) {
        formData.append('file', this.currentFile, this.currentFile.name);
      } else {
        alert('Veuillez sélectionner une image valide de moins de 5MB.');
        return;
      }
    }
  
    const serviceCall = this.isEditing && produitData.codeProduit
      ? this.produitService.updateProduit(produitData.codeProduit, formData)
      : this.produitService.createProduit(formData);
  
    serviceCall.subscribe({
      next: () => {
        this.loadProduits();
        this.resetForm();
      },
      error: (err: HttpErrorResponse) => {
        console.error('Erreur sauvegarde:', err.message);
      }
    });
  }
  
  editProduit(produit: ProduitResponse): void {
    if (!this.isMarketing) return;
  
    Swal.fire({
      title: 'Edit Product',
      html: `
        <div class="swal-form">
          <input id="swal-libelle" class="swal2-input" placeholder="Name" value="${produit.libelle}" />
          <input id="swal-prix" class="swal2-input" placeholder="Price" type="number" value="${produit.prix}" />
          <input id="swal-poids" class="swal2-input" placeholder="Weight" type="number" value="${produit.poids}" />
          <textarea id="swal-description" class="swal2-textarea" placeholder="Description">${produit.description}</textarea>
          ${this.isMarketing ? `
            <select id="swal-gamme" class="swal2-select">
              <option disabled selected value="">Select Category</option>
              ${this.gammes.map(g => `<option value="${g.codeGamme}">${g.libelle}</option>`).join('')}
            </select>
      
            <select id="swal-marque" class="swal2-select">
              <option disabled selected value="">Select Brand</option>
              ${this.marques.map(m => `<option value="${m.codeMarque}">${m.libelle}</option>`).join('')}
            </select>
          ` : ''}
          <div class="file-input-wrapper">
            <label for="swal-image" class="file-input-label">
              <i class="fas fa-upload upload-icon"></i> Upload Image
            </label>
            <input id="swal-image" type="file" class="display: none;" />
            
          </div>
  
          <div id="swal-preview-container" class="image-preview-container">
            ${produit.image
              ? `<img src="http://localhost:8080/api/produits/images/${produit.image}" class="image-preview" alt="Preview">`
              : ''}
          </div>
        </div>
      `,
      confirmButtonText: 'Save Changes',
      cancelButtonText: 'Cancel',
      showCancelButton: true,
      focusConfirm: false,
      customClass: {
        popup: 'custom-swal-popup',
        confirmButton: 'primary-btn',
        cancelButton: 'secondary-btn'
      },
      didOpen: () => {
        const fileInput = document.getElementById('swal-image') as HTMLInputElement;
        const fileNameDisplay = document.getElementById('file-name');
        const preview = document.getElementById('swal-preview-container');
  
        if (fileInput && fileNameDisplay) {
          fileInput.addEventListener('change', () => {
            const file = fileInput.files?.[0];
            fileNameDisplay.textContent = file?.name || 'No file chosen';
  
            if (file && preview) {
              const reader = new FileReader();
              reader.onload = () => {
                preview.innerHTML = `<img src="${reader.result}" class="image-preview" alt="Preview">`;
              };
              reader.readAsDataURL(file);
            }
          });
        }
      },
      preConfirm: () => {
        const libelle = normalizeText((document.getElementById('swal-libelle') as HTMLInputElement).value.trim());
        const prix = parseFloat((document.getElementById('swal-prix') as HTMLInputElement).value);
        const poids = parseFloat((document.getElementById('swal-poids') as HTMLInputElement).value);
        const description = normalizeText((document.getElementById('swal-description') as HTMLTextAreaElement).value.trim());
        const imageFile = (document.getElementById('swal-image') as HTMLInputElement).files?.[0];
  
        if (!libelle || isNaN(prix) || isNaN(poids) || !description) {
          Swal.showValidationMessage('Veuillez remplir tous les champs correctement.');
          return;
        }
        function normalizeText(text: string): string {
          return text
            .replace(/[’‘]/g, "'")           // apostrophes typographiques → apostrophe simple
            .replace(/[“”]/g, '"')           // guillemets typographiques → guillemet simple
            .replace(/[^\x00-\xFF]/g, '');   // supprime tout caractère > 255
        }
  
        return { codeProduit: produit.codeProduit, libelle, prix, poids, description, imageFile };
      }
    }).then(result => {
      if (result.isConfirmed && result.value) {
        const data = result.value;
        this.produitForm.patchValue({
          codeProduit: data.codeProduit, // ✅ Important fix
          libelle: data.libelle,
          prix: data.prix,
          poids: data.poids,
          description: data.description
        });
        this.currentFile = data.imageFile || null;
        this.isEditing = true;
        this.saveProduit();
      }
    });
  }
  deleteProduit(codeProduit: number): void {
    if (!this.isMarketing) return;
    Swal.fire({
      title: 'Are you sure?',
      text: 'This product will be permanently deleted.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it',
      cancelButtonText: 'Cancel',
      confirmButtonColor: '#e74c3c'
    }).then(result => {
      if (result.isConfirmed) {
        this.produitService.deleteProduit(codeProduit).subscribe({
          next: () => {
            this.loadProduits(); // refresh
            Swal.fire('Deleted!', 'Product has been deleted.', 'success');
          },
          error: (err) => {
            console.error('Erreur suppression:', err);
            Swal.fire('Error', 'Failed to delete product.', 'error');
          }
        });
      }
    });
  }
  

  // Logistics functions
  openStockManagement(produit: ProduitResponse): void {
    if (!this.isLogistics) return;
  
    this.router.navigate(['/dashboard/logistique/produits', produit.codeProduit, 'stocks']);
  }
  
  

  closeStockManagement(): void {
    this.showStockManagement = false;
    this.currentProduct = null;
    this.currentStocks = [];
  }

  updateStock(stock: Stock): void {
    if (!this.isLogistics) return;
    
    stock.inStock = stock.nombreStock > 0;
    this.stockService.updateStock(stock).subscribe({
      next: () => this.loadProduits(),
      error: err => console.error(err)
    });
  }

  getCouleurLibelle(codeCouleur: number): string {
    return this.currentProduct?.couleurs?.find(c => c.codeCouleur === codeCouleur)?.libelle || 'N/A';
  }

  // Common functions
  toggleProductStatus(codeProduit: number): void {
    this.produitService.toggleProductStatus(codeProduit).subscribe({
      next: () => this.loadProduits(),
      error: err => console.error(err)
    });
  }

  onImageChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.currentFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        const previewContainer = document.getElementById('swal-preview-container');
        if (previewContainer) {
          previewContainer.innerHTML = `<img src="${reader.result}" class="image-preview" alt="Preview">`;
        }
      };
      reader.readAsDataURL(file);
    }
  }
  

  resetForm(): void {
    this.produitForm.reset({ actif: true });
    this.currentFile = null;
    this.currentImage = null;
    this.isEditing = false;
  }
  
}