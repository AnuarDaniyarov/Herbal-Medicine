import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SearchResponse } from './api.models';

@Injectable({ providedIn: 'root' })
export class SearchApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;

  search(params: { q: string; type?: string; claimType?: string; page?: number; size?: number }): Observable<SearchResponse> {
    let p = new HttpParams().set('q', params.q);
    if (params.type && params.type !== 'all') p = p.set('type', params.type);
    if (params.claimType && params.claimType !== 'all') p = p.set('claimType', params.claimType);
    if (params.page !== undefined) p = p.set('page', params.page);
    if (params.size !== undefined) p = p.set('size', params.size);

    return this.http.get<SearchResponse>(`${this.base}/search`, { params: p });
  }
}
