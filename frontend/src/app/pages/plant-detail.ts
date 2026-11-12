import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { KnowledgeApiService } from '../core/knowledge-api.service';
import { AuthApiService } from '../core/auth-api.service';
import { FavoriteApiService } from '../core/favorite-api.service';
import { CompoundSummary, Extract, KnowledgeClaim, Plant, PlantSample } from '../core/api.models';

@Component({
  selector: 'app-plant-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './plant-detail.html'
})
export class PlantDetailPage {
  private readonly api = inject(ApiService);
  private readonly knowledgeApi = inject(KnowledgeApiService);
  private readonly authApi = inject(AuthApiService);
  private readonly favoriteApi = inject(FavoriteApiService);

  readonly plant = signal<Plant | null>(null);
  readonly samples = signal<PlantSample[]>([]);
  readonly compounds = signal<CompoundSummary[]>([]);
  readonly claims = signal<KnowledgeClaim[]>([]);
  readonly extractsBySample = signal<Record<number, Extract[]>>({});
  readonly isFavorite = signal(false);
  readonly favoriteId = signal<number | null>(null);
  readonly error = signal('');
  readonly notice = signal('');

  constructor() {
    const id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));
    this.api.plant(id).subscribe({
      next: value => {
        this.plant.set(value);
        this.checkFavorite(value.id);
      },
      error: err => this.error.set(err.error?.message || 'Растение не найдено.')
    });

    this.api.plantSamples(id).subscribe({
      next: page => {
        this.samples.set(page.content);
        // Batch extracts from response without N+1 HTTP calls
        const extractMap: Record<number, Extract[]> = {};
        for (const sample of page.content) {
          if (sample.extracts && sample.extracts.length) {
            extractMap[sample.id] = sample.extracts;
          }
        }
        this.extractsBySample.set(extractMap);
      }
    });

    this.api.plantCompounds(id).subscribe(page => this.compounds.set(page.content));
    this.knowledgeApi.plantClaims(id).subscribe({
      next: page => this.claims.set(page.content),
      error: () => this.claims.set([])
    });
  }

  checkFavorite(plantId: number): void {
    if (!this.authApi.currentAccount()) return;
    this.favoriteApi.check({ plantId }).subscribe({
      next: res => {
        this.isFavorite.set(res.isFavorite);
        this.favoriteId.set(res.favoriteId || null);
      }
    });
  }

  toggleFavorite(): void {
    const p = this.plant();
    if (!p) return;
    if (!this.authApi.currentAccount()) {
      this.error.set('Войдите в аккаунт, чтобы сохранять растения в избранное.');
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
      this.favoriteApi.add({ plantId: p.id }).subscribe({
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
