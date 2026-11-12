import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map, switchMap } from 'rxjs';
import { AssistantHistoryEntry, AssistantRequest, AssistantResponse, PageResponse } from './api.models';

@Injectable({ providedIn: 'root' })
export class AssistantApiService {
  private readonly http = inject(HttpClient);

  history(): Observable<PageResponse<AssistantHistoryEntry>> {
    return this.http.get<PageResponse<AssistantHistoryEntry>>('/api/assistant/history');
  }

  ask(request: AssistantRequest): Observable<AssistantResponse> {
    return this.http.get<{ token: string }>('/api/auth/csrf').pipe(
      map(() => void 0),
      switchMap(() => this.http.post<AssistantResponse>('/api/assistant/questions', request))
    );
  }
}

