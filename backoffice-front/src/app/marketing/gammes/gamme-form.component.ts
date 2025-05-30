import { Component, OnInit } from '@angular/core';
import { GammeService } from '../../../app/services/gamme.service'; // Assurez-vous que le chemin est correct
import { CommonModule } from '@angular/common';
import { Gamme } from '../../models/gamme.model'; // Assurez-vous que le chemin est correct
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-gamme-form',
  standalone:true,
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './gamme-form.component.html',
  styleUrls: ['./gamme-form.component.scss']
})
export class GammeFormComponent implements OnInit {
  gamme: Gamme = { codeGamme: 0, libelle : '', flag: true };
  isEdit = false;

  constructor(
    private gammeService: GammeService,
    public router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const codeGamme = this.route.snapshot.params['codeGamme'];
    if (codeGamme) {
      this.isEdit = true;
      this.gammeService.getAllGammes().subscribe(gammes => {
        const found = gammes.find(g => g.codeGamme == codeGamme);
        if (found) this.gamme = found;
      });
    }
  }

  onSubmit(): void {
    if (this.isEdit) {
        this.gammeService.updateGamme(this.gamme).subscribe(() => {
            this.router.navigate(['/gammes']);
          });
          
    } else {
      this.gammeService.createGamme(this.gamme).subscribe(() => {
        this.router.navigate(['/gammes']);
      });
    }
  }
}
