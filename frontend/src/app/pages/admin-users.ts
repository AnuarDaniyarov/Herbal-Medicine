import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { finalize } from 'rxjs';
import { AuthApiService } from '../core/auth-api.service';
import { PageResponse, UserAccountView, UserRole } from '../core/api.models';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './admin-users.html'
})
export class AdminUsersPage {
  private readonly api = inject(AuthApiService);
  readonly result = signal<PageResponse<UserAccountView> | null>(null);
  readonly error = signal('');
  readonly savingId = signal<number | null>(null);
  readonly loading = signal(false);
  readonly notice = signal('');
  readonly roles: UserRole[] = ['USER', 'EXPERT', 'ADMIN'];
  readonly pageIndex = signal(0);

  constructor() { this.load(); }

  load(pageIndex = this.pageIndex()): void {
    this.loading.set(true);
    this.error.set('');
    this.api.adminUsers(pageIndex).pipe(finalize(() => this.loading.set(false))).subscribe({
      next: page => { this.result.set(page); this.pageIndex.set(pageIndex); },
      error: err => this.error.set(err?.status === 401 || err?.status === 403 ? 'Для этого раздела нужна роль администратора.' : 'Не удалось загрузить список аккаунтов.')
    });
  }

  changePage(pageIndex: number): void {
    if (pageIndex >= 0 && pageIndex !== this.pageIndex()) this.load(pageIndex);
  }

  updateRoleFromEvent(user: UserAccountView, event: Event): void {
    if (!(event.target instanceof HTMLSelectElement)) return;
    this.updateRole(user, event.target.value, event.target);
  }

  updateRole(user: UserAccountView, value: string, select?: HTMLSelectElement): void {
    const role = value as UserRole;
    if (!this.roles.includes(role) || role === user.role) return;
    this.notice.set('');
    this.error.set('');
    this.savingId.set(user.id);
    this.api.updateUserRole(user.id, role).subscribe({
      next: updated => {
        this.result.update(page => page ? { ...page, content: page.content.map(item => item.id === updated.id ? updated : item) } : page);
        this.notice.set(`Роль пользователя ${updated.email} изменена на ${updated.role}.`);
        this.savingId.set(null);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Не удалось изменить роль.');
        if (select) select.value = user.role;
        this.savingId.set(null);
      }
    });
  }
}
