import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PageResponse, Preparation, PreparationIngredient } from './api.models';
import { toHttpParams } from './api-params';

@Injectable({ providedIn: 'root' })
export class PreparationApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;

  preparations(query: { query?: string; dosageForm?: string; page?: number; size?: number } = {}): Observable<PageResponse<Preparation>> {
    return this.http.get<PageResponse<Preparation>>(`${this.base}/preparations`, { params: toHttpParams(query) });
  }

  preparation(id: number): Observable<Preparation> {
    return this.http.get<Preparation>(`${this.base}/preparations/${id}`);
  }

  ingredients(id: number): Observable<PreparationIngredient[]> {
    return this.http.get<PreparationIngredient[]>(`${this.base}/preparations/${id}/ingredients`);
  }
}
