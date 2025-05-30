import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from '../models/stock.model';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  private apiUrl = 'http://localhost:8080/api/stocks';

  constructor(private http: HttpClient) {}

  // Get stocks by product ID (returns Stock model)
  getStocksByProduit(codeProduit: number): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.apiUrl}/produit/${codeProduit}`);
  }

  // Create a new stock entry (sends Stock model)
  addStock(stock: Stock): Observable<Stock> {
    return this.http.post<Stock>(this.apiUrl, stock);
  }
  
  // Update an existing stock entry (sends Stock model)
  updateStock(stock: Stock): Observable<Stock> {
    return this.http.put<Stock>(`${this.apiUrl}/${stock.codeStock}`, stock);
  }

  // Delete stock entry
  deleteStock(codeStock: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codeStock}`);
  }
}