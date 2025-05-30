import { Couleur } from "./couleur.model";


export interface ProduitResponse {
  codeProduit: number;             // ID du produit
  libelle: string;                 // Libellé du produit
  prix: number;                    // Prix HT
  poids: number;                   // Poids
  description: string;             // Description
  actif: boolean;                  // Statut actif ou non
  libelleGamme: string;             // Nom de la gamme
  libelleMarque: string;  
  codeGamme: number;     // ✅ Add this
  codeMarque: number;           // Nom de la marque
  couleurs: Couleur[];    // Liste des couleurs disponibles
  image: string;                    // Image du produit (chemin ou nom du fichier)
}
