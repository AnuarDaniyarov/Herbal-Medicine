import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
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
  readonly roles: UserRole[] = ['USER', 'EXPERT', 'ADMIN'];

  constructor() { this.load(); }

  load(): void {
    this.error.set('');
    this.api.adminUsers().subscribe({ next: page => this.result.set(page), error: err => this.error.set(err?.status === 401 || err?.status === 403 ? 'Для этого раздела нужна роль администратора.' : 'Не удалось загрузить список аккаунтов.') });
  }

  updateRole(user: UserAccountView, value: string): void {
    const role = value as UserRole;
    if (!this.roles.includes(role) || role === user.role) return;
    this.savingId.set(user.id);
    this.api.updateUserRole(user.id, role).subscribe({
      next: updated => {
        this.result.update(page => page ? { ...page, content: page.content.map(item => item.id === updated.id ? updated : item) } : page);
        this.savingId.set(null);
      },
      error: err => {
        this.error.set(err?.error?.message || 'Не удалось изменить роль.');
        this.savingId.set(null);
      }
    });
  }
}
