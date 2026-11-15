import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

interface AdminStats {
  plants: number;
  compounds: number;
  preparations: number;
  sources: number;
  users: number;
  totalClaims: number;
  claimsByStatus: Record<string, number>;
  totalRequests: number;
  averageLatencyMs: number;
  maxLatencyMs: number;
  topEndpoints: Record<string, number>;
}

@Component({
  selector: 'app-admin-stats',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="stats-page">
      <nav class="breadcrumb"><a routerLink="/">Главная</a> / <a routerLink="/admin/users">Админ</a> / Статистика</nav>
      <h1>Статистика системы</h1>

      <div *ngIf="loading()" class="loading">Загрузка...</div>
      <div *ngIf="error()" class="alert error">{{error()}}</div>

      <div *ngIf="!loading() && stats()" class="stats-grid">
        <div class="stats-section">
          <h2>База данных</h2>
          <div class="stat-cards">
            <div class="stat-card">
              <div class="stat-value">{{stats()?.plants}}</div>
              <div class="stat-label">Растений</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{stats()?.compounds}}</div>
              <div class="stat-label">Соединений</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{stats()?.preparations}}</div>
              <div class="stat-label">Препаратов</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{stats()?.sources}}</div>
              <div class="stat-label">Источников</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{stats()?.users}}</div>
              <div class="stat-label">Пользователей</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{stats()?.totalClaims}}</div>
              <div class="stat-label">Утверждений</div>
            </div>
          </div>
        </div>

        <div class="stats-section">
          <h2>Утверждения по статусу</h2>
          <div class="stat-cards">
            <div class="stat-card" *ngFor="let entry of claimsByStatusArray()">
              <div class="stat-value" [style.color]="statusColor(entry.key)">{{entry.value}}</div>
              <div class="stat-label">{{statusLabel(entry.key)}}</div>
            </div>
          </div>
        </div>

        <div class="stats-section">
          <h2>Метрики запросов</h2>
          <div class="stat-cards">
            <div class="stat-card">
              <div class="stat-value">{{stats()?.totalRequests}}</div>
              <div class="stat-label">Всего запросов</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{stats()?.averageLatencyMs}}ms</div>
              <div class="stat-label">Средняя latency</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{stats()?.maxLatencyMs}}ms</div>
              <div class="stat-label">Максимальная latency</div>
            </div>
          </div>
        </div>

        <div class="stats-section" *ngIf="stats()?.topEndpoints">
          <h2>Топ эндпоинтов</h2>
          <table class="endpoints-table">
            <thead>
              <tr><th>Эндпоинт</th><th>Запросов</th></tr>
            </thead>
            <tbody>
              <tr *ngFor="let entry of topEndpointsArray()">
                <td>{{entry.key}}</td>
                <td>{{entry.value}}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .stats-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .breadcrumb { margin-bottom: 20px; font-size: 14px; }
    .breadcrumb a { color: #0066cc; text-decoration: none; }
    h1 { margin-bottom: 24px; }
    .stats-grid { display: flex; flex-direction: column; gap: 24px; }
    .stats-section { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
    .stats-section h2 { margin-bottom: 16px; font-size: 18px; color: #333; }
    .stat-cards { display: flex; flex-wrap: wrap; gap: 16px; }
    .stat-card { flex: 1; min-width: 120px; padding: 16px; background: #f8f9fa; border-radius: 8px; text-align: center; }
    .stat-value { font-size: 28px; font-weight: 700; color: #0066cc; }
    .stat-label { font-size: 14px; color: #666; margin-top: 4px; }
    .endpoints-table { width: 100%; border-collapse: collapse; }
    .endpoints-table th, .endpoints-table td { padding: 8px 12px; text-align: left; border-bottom: 1px solid #eee; }
    .endpoints-table th { background: #f8f9fa; font-weight: 600; }
    .loading { text-align: center; padding: 40px; color: #666; }
    .alert.error { padding: 10px 16px; border-radius: 4px; background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
  `]
})
export class AdminStatsPage implements OnInit {
  private readonly http = inject(HttpClient);

  stats = signal<AdminStats | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.http.get<AdminStats>('/api/admin/stats').subscribe({
      next: (s) => { this.stats.set(s); this.loading.set(false); },
      error: () => { this.error.set('Не удалось загрузить статистику'); this.loading.set(false); }
    });
  }

  claimsByStatusArray(): { key: string; value: number }[] {
    const obj = this.stats()?.claimsByStatus ?? {};
    return Object.entries(obj).map(([key, value]) => ({ key, value: value as number }));
  }

  topEndpointsArray(): { key: string; value: number }[] {
    const obj = this.stats()?.topEndpoints ?? {};
    return Object.entries(obj).map(([key, value]) => ({ key, value: value as number }));
  }

  statusColor(status: string): string {
    switch (status) {
      case 'VERIFIED': return '#28a745';
      case 'PENDING': return '#ffc107';
      case 'REJECTED': return '#dc3545';
      default: return '#6c757d';
    }
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'VERIFIED': return 'Проверено';
      case 'PENDING': return 'На проверке';
      case 'REJECTED': return 'Отклонено';
      case 'UNVERIFIED': return 'Не проверено';
      default: return status;
    }
  }
}
