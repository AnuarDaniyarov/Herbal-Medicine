import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Prediction, PredictionRequest } from './api.models';

@Injectable({ providedIn: 'root' })
export class PredictionApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiBaseUrl;
  predict(request: PredictionRequest): Observable<Prediction> { return this.http.post<Prediction>(`${this.base}/predictions`, request); }
  prediction(id: number): Observable<Prediction> { return this.http.get<Prediction>(`${this.base}/predictions/${id}`); }
}
