import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SearchApiService } from '../core/search-api.service';
import { SearchResponse, SearchResult } from '../core/api.models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './home.html'
})
export class HomePage {
  private readonly searchApi = inject(SearchApiService);

  readonly popularQueries = [
    'растения от бессонницы',
    'зверобой противопоказания',
    'валериана взаимодействия',
    'мята показания'
  ];

  query = '';
  readonly loading = signal(false);
  readonly error = signal('');
  readonly result = signal<SearchResponse | null>(null);
  readonly searched = signal(false);

  runSearch(value?: string): void {
    const q = (value ?? this.query).trim();
    this.query = q;
    if (!q) {
      this.result.set(null);
      this.searched.set(false);
      this.error.set('');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.searched.set(true);
    this.searchApi.search(q).subscribe({
      next: response => {
        this.result.set(response);
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Не удалось выполнить поиск по базе.');
        this.loading.set(false);
      }
    });
  }

  typeLabel(item: SearchResult): string {
    if (item.type === 'claim') {
      if (item.category === 'INDICATION') return 'Показание';
      if (item.category === 'CONTRAINDICATION') return 'Противопоказание';
      if (item.category === 'INTERACTION') return 'Взаимодействие';
      return 'Сведение';
    }
    if (item.type === 'plant') return 'Растение';
    if (item.type === 'compound') return 'Соединение';
    return item.type;
  }
}
