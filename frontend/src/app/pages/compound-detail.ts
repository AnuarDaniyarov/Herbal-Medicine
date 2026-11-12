import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthApiService } from '../core/auth-api.service';
import { FavoriteApiService } from '../core/favorite-api.service';
import { CompoundDetails, ActivityMeasurement } from '../core/api.models';

@Component({
  selector: 'app-compound-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './compound-detail.html'
})
export class CompoundDetailPage {
  private readonly api = inject(ApiService);
  private readonly authApi = inject(AuthApiService);
  private readonly favoriteApi = inject(FavoriteApiService);

  readonly compound = signal<CompoundDetails | null>(null);
  readonly measurements = signal<ActivityMeasurement[]>([]);
  readonly isFavorite = signal(false);
  readonly favoriteId = signal<number | null>(null);
  readonly error = signal('');
  readonly notice = signal('');

  constructor() {
    const id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));
    this.api.compound(id).subscribe({
      next: v => {
        this.compound.set(v);
        this.checkFavorite(v.id);
      },
      error: e => this.error.set(e.error?.message || 'Соединение не найдено')
    });
    this.api.measurements(id).subscribe({
      next: v => this.measurements.set(v),
      error: () => {}
    });
  }

  checkFavorite(compoundId: number): void {
    if (!this.authApi.currentAccount()) return;
    this.favoriteApi.check({ compoundId }).subscribe({
      next: res => {
        this.isFavorite.set(res.isFavorite);
        this.favoriteId.set(res.favoriteId || null);
      }
    });
  }

  toggleFavorite(): void {
    const c = this.compound();
    if (!c) return;
    if (!this.authApi.currentAccount()) {
      this.error.set('Войдите в аккаунт, чтобы сохранять соединения в избранное.');
      return;
    }

    if (this.isFavorite() && this.favoriteId()) {
      this.favoriteApi.remove(this.favoriteId()!).subscribe({
        next: () => {
          this.isFavorite.set(false);
          this.favoriteId.set(null);
          this.notice.set('Удалено из избранного.');
        }
      });
    } else {
      this.favoriteApi.add({ compoundId: c.id }).subscribe({
        next: fav => {
          this.isFavorite.set(true);
          this.favoriteId.set(fav.id);
          this.notice.set('Добавлено в избранное.');
        },
        error: err => this.error.set(err.error?.message || 'Не удалось сохранить в избранное.')
      });
    }
  }
}
