import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './home.html'
})
export class HomePage {
  private readonly router = inject(Router);
  readonly plants = signal<number | null>(null);
  readonly compounds = signal<number | null>(null);

  searchQuery = '';
  readonly popularQueries = [
    'растения от бессонницы',
    'зверобой противопоказания',
    'что из трав помогает при кашле',
    'валериана взаимодействия',
    'ромашка аптечная'
  ];

  constructor() {
    const api = inject(ApiService);
    api.plants({ page: 0, size: 1 }).subscribe(v => this.plants.set(v.totalElements));
    api.compounds({ page: 0, size: 1 }).subscribe(v => this.compounds.set(v.totalElements));
  }

  onSearch(): void {
    const q = this.searchQuery.trim();
    if (q) {
      this.router.navigate(['/search'], { queryParams: { q } });
    }
  }

  selectPopular(q: string): void {
    this.searchQuery = q;
    this.router.navigate(['/search'], { queryParams: { q } });
  }
}
