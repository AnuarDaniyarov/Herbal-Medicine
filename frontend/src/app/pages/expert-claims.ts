import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthApiService } from '../core/auth-api.service';
import { KnowledgeClaim, PageResponse } from '../core/api.models';
import { of, switchMap } from 'rxjs';

@Component({
  selector: 'app-expert-claims',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './expert-claims.html'
})
export class ExpertClaimsPage {
  private readonly api = inject(AuthApiService);
  readonly result = signal<PageResponse<KnowledgeClaim> | null>(null);
  readonly error = signal('');
  readonly savingId = signal<number | null>(null);
  readonly notice = signal('');
  readonly loading = signal(false);
  readonly pageIndex = signal(0);

  // Editing state
  readonly editingId = signal<number | null>(null);
  editTitle = '';
  editClaimText = '';
  editInteractionPartner = '';
  editSourceLocator = '';
  editReviewNote = '';

  constructor() { this.load(); }

  load(pageIndex = this.pageIndex()): void {
    this.loading.set(true);
    this.error.set('');
    this.api.expertQueue(pageIndex).subscribe({
      next: page => {
        this.result.set(page);
        this.pageIndex.set(pageIndex);
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err?.status === 401 || err?.status === 403
          ? 'Очередь доступна экспертам и администраторам после входа.'
          : 'Не удалось загрузить очередь проверки.');
        this.loading.set(false);
      }
    });
  }

  changePage(pageIndex: number): void {
    if (pageIndex >= 0 && pageIndex !== this.pageIndex()) this.load(pageIndex);
  }

  startEdit(claim: KnowledgeClaim): void {
    this.editingId.set(claim.id);
    this.editTitle = claim.title || '';
    this.editClaimText = claim.claimText || '';
    this.editInteractionPartner = claim.interactionPartner || '';
    this.editSourceLocator = claim.sourceLocator || '';
    this.editReviewNote = '';
  }

  cancelEdit(): void {
    this.editingId.set(null);
  }

  saveEdit(claim: KnowledgeClaim, reviewStatus?: 'VERIFIED' | 'REJECTED'): void {
    this.savingId.set(claim.id);
    this.notice.set('');
    this.error.set('');

    const editPayload = {
      title: this.editTitle.trim(),
      claimText: this.editClaimText.trim(),
      interactionPartner: this.editInteractionPartner.trim() || undefined,
      sourceLocator: this.editSourceLocator.trim() || undefined,
    };
    const reviewNote = this.editReviewNote.trim() || undefined;
    const saveRequest = this.api.updateClaim(claim.id, editPayload).pipe(
      switchMap(updated => reviewStatus
        ? this.api.reviewClaim(updated.id, { reviewStatus, reviewNote })
        : of(updated))
    );

    saveRequest.subscribe({
      next: updated => {
        if (reviewStatus) {
          this.removeFromQueue(updated.id);
          this.notice.set(reviewStatus === 'VERIFIED'
            ? 'Данные отредактированы, утверждены и опубликованы.'
            : 'Утверждение отклонено.');
        } else {
          // If just updated, update the item in place
          this.result.update(page => page ? {
            ...page,
            content: page.content.map(item => item.id === updated.id ? updated : item)
          } : page);
          this.notice.set('Данные карточки успешно обновлены экспертом.');
        }
        this.editingId.set(null);
        this.savingId.set(null);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Не удалось сохранить изменения эксперта.');
        this.savingId.set(null);
      }
    });
  }

  review(claim: KnowledgeClaim, reviewStatus: 'VERIFIED' | 'REJECTED'): void {
    this.savingId.set(claim.id);
    this.notice.set('');
    this.error.set('');
    this.api.reviewClaim(claim.id, { reviewStatus }).subscribe({
      next: updated => {
        this.removeFromQueue(updated.id);
        this.notice.set(reviewStatus === 'VERIFIED' ? 'Утверждение проверено и опубликовано.' : 'Утверждение отклонено.');
        this.savingId.set(null);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Не удалось сохранить решение эксперта.');
        this.savingId.set(null);
      }
    });
  }

  private removeFromQueue(id: number): void {
    const page = this.result();
    if (!page) return;
    const content = page.content.filter(item => item.id !== id);
    if (content.length === 0 && this.pageIndex() > 0) {
      this.load(this.pageIndex() - 1);
      return;
    }
    const totalElements = Math.max(0, page.totalElements - 1);
    const totalPages = Math.ceil(totalElements / page.size);
    this.result.set({
      ...page,
      content,
      totalElements,
      totalPages,
      first: this.pageIndex() === 0,
      last: this.pageIndex() >= totalPages - 1
    });
  }
}
