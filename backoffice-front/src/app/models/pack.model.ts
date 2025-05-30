export interface PackProduit {
    codeProduit: number;
    reductionPourcentage: number;
    reductionValeur: number;
  }
  
  export interface Pack {
    id?: number;
    nom: string;
    description: string;
    actif: boolean;
    produits: PackProduit[];
  }
  