import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PreparationApiService } from '../core/preparation-api.service';
import { PageResponse, Preparation } from '../core/api.models';

@Component({
  selector: 'app-preparations',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './preparations.html'
})
export class PreparationsPage {
  private readonly api = inject(PreparationApiService);
  readonly result = signal<PageResponse<Preparation> | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  query = '';
  dosageForm = '';
  page = 0;

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.api.preparations({ query: this.query, dosageForm: this.dosageForm, page: this.page, size: 24 })
      .subscribe({
        next: res => {
          this.result.set(res);
          this.loading.set(false);
        },
        error: err => {
          this.error.set(err.error?.message || 'Не удалось загрузить каталог препаратов');
          this.loading.set(false);
        }
      });
  }

  search(): void {
    this.page = 0;
    this.load();
  }

  move(n: number): void {
    this.page = Math.max(0, Math.min((this.result()?.totalPages || 1) - 1, this.page + n));
    this.load();
  }
}
