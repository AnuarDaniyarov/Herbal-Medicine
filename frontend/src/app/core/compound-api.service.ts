import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActivityMeasurement, CompoundDetails, CompoundSummary, PageResponse } from './api.models';
import { CatalogQuery, toHttpParams } from './api-params';

@Injectable({ providedIn: 'root' })
export class CompoundApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;
  compounds(query: CatalogQuery = {}): Observable<PageResponse<CompoundSummary>> { return this.http.get<PageResponse<CompoundSummary>>(`${this.base}/compounds`, { params: toHttpParams(query) }); }
  compound(id: number): Observable<CompoundDetails> { return this.http.get<CompoundDetails>(`${this.base}/compounds/${id}`); }
  measurements(id: number): Observable<ActivityMeasurement[]> { return this.http.get<ActivityMeasurement[]>(`${this.base}/compounds/${id}/measurements`); }
}
