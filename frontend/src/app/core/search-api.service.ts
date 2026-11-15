import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SearchResponse } from './api.models';
import { toHttpParams } from './api-params';

@Injectable({ providedIn: 'root' })
export class SearchApiService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/search`;

  search(q: string, page = 0, size = 20): Observable<SearchResponse> {
    return this.http.get<SearchResponse>(this.base, {
      params: toHttpParams({ q, page, size })
    });
  }
}
