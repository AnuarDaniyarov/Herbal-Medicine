import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CompoundSummary, Extract, PageResponse, Plant, PlantSample } from './api.models';
import { CatalogQuery, toHttpParams } from './api-params';

@Injectable({ providedIn: 'root' })
export class PlantApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;
  plants(query: CatalogQuery = {}): Observable<PageResponse<Plant>> { return this.http.get<PageResponse<Plant>>(`${this.base}/plants`, { params: toHttpParams(query) }); }
  plant(id: number): Observable<Plant> { return this.http.get<Plant>(`${this.base}/plants/${id}`); }
  compounds(id: number, page = 0, size = 12): Observable<PageResponse<CompoundSummary>> { return this.http.get<PageResponse<CompoundSummary>>(`${this.base}/plants/${id}/compounds`, { params: toHttpParams({ page, size, sort: 'name,asc' }) }); }
  samples(id: number): Observable<PageResponse<PlantSample>> { return this.http.get<PageResponse<PlantSample>>(`${this.base}/plants/${id}/samples`, { params: toHttpParams({ page: 0, size: 100, sort: 'id,asc' }) }); }
  extracts(sampleId: number): Observable<Extract[]> { return this.http.get<Extract[]>(`${this.base}/samples/${sampleId}/extracts`); }
}
