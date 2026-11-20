import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { SearchResponse, SearchResult } from '../core/api.models';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './search.html'
})
export class SearchPage implements OnInit {
  private readonly api = inject(ApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  query = '';
  selectedType = 'all';
  selectedClaimType = 'all';

  readonly response = signal<SearchResponse | null>(null);
  readonly loading = signal(false);
  readonly error = signal('');

  readonly popularQueries = [
    'растения от бессонницы',
    'зверобой противопоказания',
    'что из трав помогает при кашле',
    'валериана взаимодействия',
    'ромашка аптечная'
  ];

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.query = params['q'] || '';
      this.selectedType = params['type'] || 'all';
      this.selectedClaimType = params['claimType'] || 'all';
      if (this.query.trim()) {
        this.performSearch();
      }
    });
  }

  submit(): void {
    const q = this.query.trim();
    if (!q) return;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        q,
        type: this.selectedType !== 'all' ? this.selectedType : null,
        claimType: this.selectedClaimType !== 'all' ? this.selectedClaimType : null
      },
      queryParamsHandling: 'merge'
    });
  }

  setPopular(q: string): void {
    this.query = q;
    this.submit();
  }

  filterByType(type: string): void {
    this.selectedType = type;
    this.submit();
  }

  filterByClaimType(claimType: string): void {
    this.selectedClaimType = claimType;
    this.submit();
  }

  private performSearch(): void {
    this.loading.set(true);
    this.error.set('');
    this.api.search({
      q: this.query,
      type: this.selectedType,
      claimType: this.selectedClaimType
    }).subscribe({
      next: res => {
        this.response.set(res);
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Ошибка выполнения поиска. Попробуйте снова.');
        this.loading.set(false);
      }
    });
  }

  intentLabel(intent: string | undefined): string {
    switch (intent) {
      case 'INDICATION': return '🔍 Поиск по показанию / симптому';
      case 'CONTRAINDICATION': return '⚠️ Поиск по противопоказаниям';
      case 'INTERACTION': return '🔄 Поиск по взаимодействиям препаратов';
      default: return '📖 Комплексный поиск по базе знаний';
    }
  }

  categoryLabel(type: string, category?: string): string {
    if (type === 'plant') return 'Растение';
    if (type === 'preparation') return 'Препарат';
    if (type === 'compound') return 'Соединение';
    if (category === 'INDICATION') return 'Показание';
    if (category === 'CONTRAINDICATION') return 'Противопоказание';
    if (category === 'INTERACTION') return 'Взаимодействие';
    return category || type;
  }
}
