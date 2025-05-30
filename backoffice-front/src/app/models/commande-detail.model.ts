export interface CommandeDetail {
    codeCommande: number;
    dateCommande: string;
    client: {
      nom: string;
      username: string;
      prenom: string;
      email: string;
      tel: string;
      cin: string;
        rue: string;
        ville: string;
        codePostal: string;
    };
    lignes: {
      libelleProduit: string;
      prix: number;
      quantite: number;
      poids: number;
    }[];
    modePaiement: string;
    sousTotal: number;
    tva: number;
    total: number;
    status: 'PENDING' | 'VALIDATED' | 'REFUSED';
  }