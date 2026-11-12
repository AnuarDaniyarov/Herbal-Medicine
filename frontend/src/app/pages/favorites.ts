import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FavoriteApiService } from '../core/favorite-api.service';
import { AuthApiService } from '../core/auth-api.service';
import { FavoriteItem } from '../core/api.models';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './favorites.html'
})
export class FavoritesPage {
  private readonly api = inject(FavoriteApiService);
  readonly authApi = inject(AuthApiService);
  readonly items = signal<FavoriteItem[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly notice = signal('');

  constructor() {
    this.load();
  }

  load(): void {
    if (!this.authApi.currentAccount()) {
      this.loading.set(false);
      this.error.set('Войдите в аккаунт, чтобы просматривать избранное.');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.api.favorites().subscribe({
      next: data => {
        this.items.set(data);
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err.error?.message || 'Не удалось загрузить избранное.');
        this.loading.set(false);
      }
    });
  }

  remove(item: FavoriteItem): void {
    this.notice.set('');
    this.api.remove(item.id).subscribe({
      next: () => {
        this.items.update(list => list.filter(i => i.id !== item.id));
        this.notice.set(`«${item.title}» удалено из избранного.`);
      },
      error: err => this.error.set(err.error?.message || 'Не удалось удалить запись.')
    });
  }
}
