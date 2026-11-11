import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ActivityMeasurement, Assay, ComparisonResponse, CompoundDetails, CompoundSummary, Extract, PageResponse, Plant, PlantSample, Prediction, PredictionRequest, Source, Target } from './api.models';
import { CandidateApiService } from './candidate-api.service';
import { CompoundApiService } from './compound-api.service';
import type { CatalogQuery } from './api-params';
import { PlantApiService } from './plant-api.service';
import { PredictionApiService } from './prediction-api.service';
import { ResearchApiService } from './research-api.service';

export type { CatalogQuery } from './api-params';

/** Compatibility facade for UI pages; domain API clients can be split across gateway routes/services later. */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly plantsApi = inject(PlantApiService);
  private readonly compoundsApi = inject(CompoundApiService);
  private readonly researchApi = inject(ResearchApiService);
  private readonly predictionsApi = inject(PredictionApiService);
  private readonly candidatesApi = inject(CandidateApiService);

  plants(query: CatalogQuery = {}): Observable<PageResponse<Plant>> { return this.plantsApi.plants(query); }
  plant(id: number): Observable<Plant> { return this.plantsApi.plant(id); }
  plantCompounds(id: number, page = 0, size = 12): Observable<PageResponse<CompoundSummary>> { return this.plantsApi.compounds(id, page, size); }
  plantSamples(id: number): Observable<PageResponse<PlantSample>> { return this.plantsApi.samples(id); }
  sampleExtracts(id: number): Observable<Extract[]> { return this.plantsApi.extracts(id); }
  compounds(query: CatalogQuery = {}): Observable<PageResponse<CompoundSummary>> { return this.compoundsApi.compounds(query); }
  compound(id: number): Observable<CompoundDetails> { return this.compoundsApi.compound(id); }
  measurements(id: number): Observable<ActivityMeasurement[]> { return this.compoundsApi.measurements(id); }
  assays(): Observable<Assay[]> { return this.researchApi.assays(); }
  targets(): Observable<Target[]> { return this.researchApi.targets(); }
  source(id: number): Observable<Source> { return this.researchApi.source(id); }
  predict(request: PredictionRequest): Observable<Prediction> { return this.predictionsApi.predict(request); }
  prediction(id: number): Observable<Prediction> { return this.predictionsApi.prediction(id); }
  compare(ids: number[], activityCode: string): Observable<ComparisonResponse> { return this.candidatesApi.compare(ids, activityCode); }
  exportCandidates(activityCode?: string, candidateIds?: number[]): Observable<Blob> { return this.candidatesApi.export(activityCode, candidateIds); }
}
