import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AddFavoriteRequest, FavoriteItem } from './api.models';
import { toHttpParams } from './api-params';

@Injectable({ providedIn: 'root' })
export class FavoriteApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;

  favorites(): Observable<FavoriteItem[]> {
    return this.http.get<FavoriteItem[]>(`${this.base}/favorites`);
  }

  add(request: AddFavoriteRequest): Observable<FavoriteItem> {
    return this.http.post<FavoriteItem>(`${this.base}/favorites`, request);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/favorites/${id}`);
  }

  check(params: { plantId?: number; compoundId?: number; preparationId?: number }): Observable<{ isFavorite: boolean; favoriteId?: number | null }> {
    return this.http.get<{ isFavorite: boolean; favoriteId?: number | null }>(`${this.base}/favorites/check`, {
      params: toHttpParams(params)
    });
  }
}
