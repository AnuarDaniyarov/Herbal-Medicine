import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Assay, Source, Target } from './api.models';

@Injectable({ providedIn: 'root' })
export class ResearchApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;
  assays(): Observable<Assay[]> { return this.http.get<Assay[]>(`${this.base}/assays`); }
  targets(): Observable<Target[]> { return this.http.get<Target[]>(`${this.base}/targets`); }
  source(id: number): Observable<Source> { return this.http.get<Source>(`${this.base}/sources/${id}`); }
}
