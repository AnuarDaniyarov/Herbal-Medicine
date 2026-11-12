import { Component, inject, signal } from '@angular/core';
import { AuthApiService } from '../core/auth-api.service';
import { KnowledgeClaim, PageResponse } from '../core/api.models';

@Component({
  selector: 'app-expert-claims',
  standalone: true,
  imports: [],
  templateUrl: './expert-claims.html'
})
export class ExpertClaimsPage {
  private readonly api = inject(AuthApiService);
  readonly result = signal<PageResponse<KnowledgeClaim> | null>(null);
  readonly error = signal('');
  readonly savingId = signal<number | null>(null);
  readonly notice = signal('');

  constructor() { this.load(); }

  load(): void {
    this.error.set('');
    this.api.expertQueue().subscribe({
      next: page => this.result.set(page),
      error: err => this.error.set(err?.status === 401 || err?.status === 403
        ? 'Очередь доступна экспертам и администраторам после входа.'
        : 'Не удалось загрузить очередь проверки.')
    });
  }

  review(claim: KnowledgeClaim, reviewStatus: 'VERIFIED' | 'REJECTED'): void {
    this.savingId.set(claim.id);
    this.notice.set('');
    this.api.reviewClaim(claim.id, { reviewStatus }).subscribe({
      next: updated => {
        this.result.update(page => page ? { ...page, content: page.content.filter(item => item.id !== updated.id) } : page);
        this.notice.set(reviewStatus === 'VERIFIED' ? 'Утверждение проверено и опубликовано.' : 'Утверждение отклонено.');
        this.savingId.set(null);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Не удалось сохранить решение эксперта.');
        this.savingId.set(null);
      }
    });
  }
}
