import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ClaimType, KnowledgeClaim, PageResponse } from './api.models';

@Injectable({ providedIn: 'root' })
export class KnowledgeApiService {
  private readonly http = inject(HttpClient);

  plantClaims(plantId: number, type?: ClaimType): Observable<PageResponse<KnowledgeClaim>> {
    return this.http.get<PageResponse<KnowledgeClaim>>('/api/knowledge/claims', {
      params: { plantId, ...(type ? { type } : {}) }
    });
  }

  preparationClaims(preparationId: number, type?: ClaimType): Observable<PageResponse<KnowledgeClaim>> {
    return this.http.get<PageResponse<KnowledgeClaim>>('/api/knowledge/claims', {
      params: { preparationId, ...(type ? { type } : {}) }
    });
  }
}
