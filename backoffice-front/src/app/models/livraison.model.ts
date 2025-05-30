export enum StatusLivraison {
    CREE = 'CREE',
    VALIDEE = 'VALIDEE',
    COLLECTEE = 'COLLECTEE',
    EN_TRANSIT = 'EN_TRANSIT',
    ARRIVEE = 'ARRIVEE',
    LIVREE = 'LIVREE'
  }
  
  export interface Livraison {
    codeLivraison: number;
    numTracking: string;
    dateLivraison: string;
    shippingLabel: string;
    statusLivraison: StatusLivraison;
    codeCommande: number;
  }
  