export interface CommandeResponse {
    codeCommande: number;
    dateCommande: string;
    status: 'PENDING' | 'VALIDATED' | 'REFUSED';
    clientNom: string;
    clientPrenom: string;
  }