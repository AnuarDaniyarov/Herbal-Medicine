import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, switchMap, tap } from 'rxjs';
import { ClaimReviewRequest, CreateSourceRequest, KnowledgeClaim, PageResponse, Source, UserAccountView, UserRole, LoginRequest, RegisterRequest } from './api.models';

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/auth';
  readonly currentUser = signal<UserAccountView | null>(null);
  readonly initialized = signal(false);

  currentAccount(): UserAccountView | null { return this.currentUser(); }

  initialize(): void {
    this.http.get<UserAccountView>(`${this.base}/me`).pipe(
      catchError(() => of(null)),
      tap(user => { this.currentUser.set(user); this.initialized.set(true); })
    ).subscribe();
  }

  register(request: RegisterRequest): Observable<UserAccountView> {
    return this.csrf().pipe(switchMap(() => this.http.post<UserAccountView>(`${this.base}/register`, request)));
  }

  login(request: LoginRequest): Observable<UserAccountView> {
    return this.csrf().pipe(
      switchMap(() => this.http.post<UserAccountView>(`${this.base}/login`, request)),
      tap(user => { this.currentUser.set(user); this.initialized.set(true); })
    );
  }

  logout(): Observable<void> {
    return this.csrf().pipe(
      switchMap(() => this.http.post<void>(`${this.base}/logout`, {})),
      tap(() => this.currentUser.set(null))
    );
  }

  adminUsers(page = 0, size = 25): Observable<PageResponse<UserAccountView>> {
    return this.http.get<PageResponse<UserAccountView>>('/api/admin/users', { params: { page, size } });
  }

  updateUserRole(id: number, role: UserRole): Observable<UserAccountView> {
    return this.csrf().pipe(switchMap(() => this.http.patch<UserAccountView>(`/api/admin/users/${id}/role`, { role })));
  }

  adminSources(page = 0, size = 25): Observable<PageResponse<Source>> {
    return this.http.get<PageResponse<Source>>('/api/admin/sources', { params: { page, size } });
  }

  createSource(request: CreateSourceRequest): Observable<Source> {
    return this.csrf().pipe(switchMap(() => this.http.post<Source>('/api/admin/sources', request)));
  }

  expertQueue(page = 0, size = 25): Observable<PageResponse<KnowledgeClaim>> {
    return this.http.get<PageResponse<KnowledgeClaim>>('/api/expert/claims/queue', { params: { page, size } });
  }

  reviewClaim(id: number, request: ClaimReviewRequest): Observable<KnowledgeClaim> {
    return this.csrf().pipe(switchMap(() => this.http.patch<KnowledgeClaim>(`/api/expert/claims/${id}`, request)));
  }

  private csrf(): Observable<void> {
    return this.http.get<{ token: string }>(`${this.base}/csrf`).pipe(map(() => void 0));
  }
}
