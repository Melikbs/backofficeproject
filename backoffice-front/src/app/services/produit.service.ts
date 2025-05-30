import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ProduitResponse } from '../models/produitresponse.model';  // Ensure this model exists
import { Couleur } from '../models/couleur.model';  // Ensure this model exists

@Injectable({
  providedIn: 'root'
})
export class ProduitService {
  private apiUrl = 'http://localhost:8080/api/produits';  // API base URL

  constructor(private http: HttpClient) {}

  // Get all produits
  getAllProduits(): Observable<ProduitResponse[]> {
    return this.http.get<ProduitResponse[]>(this.apiUrl).pipe(catchError(this.handleError));
  }

  // Get a single produit by ID
  getProduitById(id: number): Observable<ProduitResponse> {
    return this.http.get<ProduitResponse>(`${this.apiUrl}/${id}`).pipe(catchError(this.handleError));
  }

  // Create a new produit with FormData (image + product data)
  createProduit(formData: FormData): Observable<any> {
    return this.http.post<any>(this.apiUrl, formData).pipe(catchError(this.handleError));
  }

  // Update a produit (with FormData)
  updateProduit(id: number, formData: FormData): Observable<ProduitResponse> {
    return this.http.put<ProduitResponse>(`${this.apiUrl}/${id}`, formData).pipe(catchError(this.handleError));
  }

  // Toggle product status (e.g., active/inactive)
  toggleProductStatus(produitId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${produitId}/toggle-activation`, {}).pipe(catchError(this.handleError));
  }

  // Get a list of couleurs for a produit
  getCouleurs(): Observable<Couleur[]> {
    return this.http.get<Couleur[]>(`${this.apiUrl}/couleurs`).pipe(catchError(this.handleError));
  }

  // Handle errors for HTTP requests
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Une erreur est survenue';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur : ${error.error.message}`;
    } else {
      errorMessage = `Erreur HTTP : ${error.status}, ${error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }
  deleteProduit(codeProduit: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codeProduit}`).pipe(
      catchError(this.handleError)
    );
  }
  
}
