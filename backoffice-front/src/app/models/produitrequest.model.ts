export interface ProduitRequest {
    codeGamme: number;    // ID de la gamme sélectionnée
    codeMarque: number;   // ID de la marque sélectionnée
    libelle: string;      // Libellé du produit
    prix: number;         // Prix HT
    poids: number;        // Poids
    description: string;  // Description du produit
    image: string;        // Image du produit (chemin ou nom du fichier)
  }
  