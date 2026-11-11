import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ComparisonResponse } from './api.models';

@Injectable({ providedIn: 'root' })
export class CandidateApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;
  compare(candidateIds: number[], activityCode: string): Observable<ComparisonResponse> { return this.http.post<ComparisonResponse>(`${this.base}/comparisons`, { candidateIds, activityCode }); }
  export(activityCode?: string, candidateIds?: number[]): Observable<Blob> {
    let params = new HttpParams();
    if (activityCode) params = params.set('activityCode', activityCode);
    for (const id of candidateIds ?? []) params = params.append('candidateIds', id);
    return this.http.get(`${this.base}/reports/candidates`, { params, responseType: 'blob' });
  }
}
