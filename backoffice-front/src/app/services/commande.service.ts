import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CommandeResponse } from "../models/commande-response.model";
import { CommandeDetail } from "../models/commande-detail.model";
import { Livraison } from "../models/livraison.model";

// src/app/services/commande.service.ts
@Injectable({
    providedIn: 'root',
  })
  export class CommandeService {
    private apiUrl = 'http://localhost:8080/api/commandes';
  
    constructor(private http: HttpClient) {}
  
    getAll(): Observable<CommandeResponse[]> {
      return this.http.get<CommandeResponse[]>(this.apiUrl);
    }
  
    getDetails(id: number): Observable<CommandeDetail> {
      return this.http.get<CommandeDetail>(`${this.apiUrl}/${id}`);
    }
  
    validate(id: number): Observable<void> {
      return this.http.put<void>(`${this.apiUrl}/${id}/validate`, {});
    }
  
    refuse(id: number): Observable<void> {
      return this.http.put<void>(`${this.apiUrl}/${id}/refuse`, {});
    }
    getLivraisonByCommande(codeCommande: number): Observable<Livraison> {
      return this.http.get<Livraison>(`http://localhost:8080/api/livraisons/commande/${codeCommande}`);
    }
    
  }
  