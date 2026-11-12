import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PreparationApiService } from '../core/preparation-api.service';
import { KnowledgeApiService } from '../core/knowledge-api.service';
import { AuthApiService } from '../core/auth-api.service';
import { FavoriteApiService } from '../core/favorite-api.service';
import { KnowledgeClaim, Preparation, PreparationIngredient } from '../core/api.models';

@Component({
  selector: 'app-preparation-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './preparation-detail.html'
})
export class PreparationDetailPage {
  private readonly api = inject(PreparationApiService);
  private readonly knowledgeApi = inject(KnowledgeApiService);
  private readonly authApi = inject(AuthApiService);
  private readonly favoriteApi = inject(FavoriteApiService);

  readonly preparation = signal<Preparation | null>(null);
  readonly ingredients = signal<PreparationIngredient[]>([]);
  readonly claims = signal<KnowledgeClaim[]>([]);
  readonly isFavorite = signal(false);
  readonly favoriteId = signal<number | null>(null);
  readonly error = signal('');
  readonly notice = signal('');

  constructor() {
    const id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));
    this.api.preparation(id).subscribe({
      next: p => {
        this.preparation.set(p);
        this.checkFavorite(p.id);
      },
      error: err => this.error.set(err.error?.message || 'Препарат не найден.')
    });

    this.api.ingredients(id).subscribe({
      next: items => this.ingredients.set(items),
      error: () => this.ingredients.set([])
    });

    this.knowledgeApi.preparationClaims(id).subscribe({
      next: page => this.claims.set(page.content),
      error: () => this.claims.set([])
    });
  }

  checkFavorite(preparationId: number): void {
    if (!this.authApi.currentAccount()) return;
    this.favoriteApi.check({ preparationId }).subscribe({
      next: res => {
        this.isFavorite.set(res.isFavorite);
        this.favoriteId.set(res.favoriteId || null);
      }
    });
  }

  toggleFavorite(): void {
    const prep = this.preparation();
    if (!prep) return;
    if (!this.authApi.currentAccount()) {
      this.error.set('Войдите в аккаунт, чтобы сохранять препараты в избранное.');
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
      this.favoriteApi.add({ preparationId: prep.id }).subscribe({
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
