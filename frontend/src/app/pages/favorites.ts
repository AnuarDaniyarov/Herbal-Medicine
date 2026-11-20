import { Component, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FavoriteApiService } from '../core/favorite-api.service';
import { AssistantApiService } from '../core/assistant-api.service';
import { AuthApiService } from '../core/auth-api.service';
import { AssistantHistoryEntry, FavoriteItem, PageResponse } from '../core/api.models';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './favorites.html'
})
export class FavoritesPage {
  private readonly api = inject(FavoriteApiService);
  private readonly assistantApi = inject(AssistantApiService);
  private readonly route = inject(ActivatedRoute);
  readonly authApi = inject(AuthApiService);

  readonly activeTab = signal<'favorites' | 'history'>('favorites');
  readonly items = signal<FavoriteItem[]>([]);
  readonly historyItems = signal<AssistantHistoryEntry[]>([]);
  readonly historyPage = signal<PageResponse<AssistantHistoryEntry> | null>(null);
  readonly historyPageIndex = signal(0);
  readonly loading = signal(true);
  readonly historyLoading = signal(false);
  readonly error = signal('');
  readonly historyError = signal('');
  readonly notice = signal('');

  private readonly accountEffect = effect(() => {
    if (this.authApi.initialized()) this.load();
  });

  constructor() {
    this.route.queryParams.subscribe(params => {
      if (params['tab'] === 'history') this.activeTab.set('history');
    });
  }

  load(): void {
    if (!this.authApi.currentAccount()) {
      this.loading.set(false);
      this.historyLoading.set(false);
      this.error.set('Войдите в аккаунт, чтобы просматривать личный кабинет.');
      return;
    }
    this.loading.set(true);
    this.error.set('');

    // Load Favorites
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

    // Load History
    this.loadHistory(0);
  }

  loadHistory(pageIndex = this.historyPageIndex()): void {
    this.historyLoading.set(true);
    this.historyError.set('');
    this.assistantApi.history(pageIndex).subscribe({
      next: page => {
        this.historyPage.set(page);
        this.historyPageIndex.set(pageIndex);
        this.historyItems.set(page.content);
        this.historyLoading.set(false);
      },
      error: err => {
        this.historyError.set(err.error?.message || 'Не удалось загрузить историю запросов.');
        this.historyLoading.set(false);
      }
    });
  }

  changeHistoryPage(pageIndex: number): void {
    if (pageIndex >= 0 && pageIndex !== this.historyPageIndex()) this.loadHistory(pageIndex);
  }

  setTab(tab: 'favorites' | 'history'): void {
    this.activeTab.set(tab);
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
