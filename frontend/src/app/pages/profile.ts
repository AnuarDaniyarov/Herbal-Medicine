import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthApiService } from '../core/auth-api.service';
import { UserAccountView } from '../core/api.models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="profile-page">
      <nav class="breadcrumb"><a routerLink="/">Главная</a> / Профиль</nav>
      <h1>Личный кабинет</h1>

      <div *ngIf="loading()" class="loading">Загрузка...</div>

      <div *ngIf="!loading() && user()" class="profile-card">
        <div class="profile-info">
          <div class="field">
            <label>Email</label>
            <p>{{user()?.email}}</p>
          </div>
          <div class="field">
            <label>Имя отображения</label>
            <p>{{user()?.displayName}}</p>
          </div>
          <div class="field">
            <label>Роль</label>
            <p [class]="roleClass(user()?.role)">{{roleLabel(user()?.role)}}</p>
          </div>
          <div class="field">
            <label>Дата регистрации</label>
            <p>{{user()?.createdAt | date:'dd.MM.yyyy'}}</p>
          </div>
        </div>

        <form (ngSubmit)="onUpdate()" class="edit-form">
          <h3>Редактировать профиль</h3>
          <div class="field">
            <label for="displayName">Имя отображения</label>
            <input id="displayName" type="text" [(ngModel)]="editName" name="displayName" required minlength="2" maxlength="200">
          </div>
          <div class="field">
            <label for="currentPassword">Текущий пароль (для смены пароля)</label>
            <input id="currentPassword" type="password" [(ngModel)]="editCurrentPassword" name="currentPassword">
          </div>
          <div class="field">
            <label for="newPassword">Новый пароль (мин. 12 символов)</label>
            <input id="newPassword" type="password" [(ngModel)]="editNewPassword" name="newPassword" minlength="12" maxlength="128">
          </div>
          <button type="submit" [disabled]="saving()">Сохранить изменения</button>
        </form>

        <div *ngIf="success()" class="alert success">{{success()}}</div>
        <div *ngIf="error()" class="alert error">{{error()}}</div>
      </div>
    </div>
  `,
  styles: [`
    .profile-page { max-width: 700px; margin: 0 auto; padding: 20px; }
    .breadcrumb { margin-bottom: 20px; font-size: 14px; }
    .breadcrumb a { color: #0066cc; text-decoration: none; }
    h1 { margin-bottom: 24px; }
    .profile-card { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
    .profile-info { margin-bottom: 24px; padding-bottom: 24px; border-bottom: 1px solid #eee; }
    .field { margin-bottom: 16px; }
    .field label { display: block; font-weight: 600; margin-bottom: 4px; color: #333; }
    .field p { margin: 0; color: #555; }
    .field input { width: 100%; padding: 8px 12px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px; box-sizing: border-box; }
    .edit-form h3 { margin-bottom: 16px; }
    .edit-form button { padding: 10px 20px; background: #0066cc; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; }
    .edit-form button:disabled { background: #999; cursor: not-allowed; }
    .alert { padding: 10px 16px; border-radius: 4px; margin-top: 16px; }
    .alert.success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .alert.error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    .role-USER { color: #28a745; }
    .role-EXPERT { color: #007bff; }
    .role-ADMIN { color: #dc3545; }
    .loading { text-align: center; padding: 40px; color: #666; }
  `]
})
export class ProfilePage implements OnInit {
  private readonly auth = inject(AuthApiService);

  user = signal<UserAccountView | null>(null);
  loading = signal(true);
  saving = signal(false);
  success = signal<string | null>(null);
  error = signal<string | null>(null);

  editName = '';
  editCurrentPassword = '';
  editNewPassword = '';

  ngOnInit(): void {
    this.auth.getProfile().subscribe({
      next: (u) => {
        this.user.set(u);
        this.editName = u.displayName;
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Не удалось загрузить профиль');
      }
    });
  }

  onUpdate(): void {
    this.saving.set(true);
    this.success.set(null);
    this.error.set(null);

    this.auth.updateProfile({
      displayName: this.editName,
      currentPassword: this.editCurrentPassword || undefined,
      newPassword: this.editNewPassword || undefined
    }).subscribe({
      next: (u) => {
        this.user.set(u);
        this.success.set('Профиль успешно обновлён');
        this.editCurrentPassword = '';
        this.editNewPassword = '';
        this.saving.set(false);
      },
      error: (err: any) => {
        this.error.set(err.error?.message || 'Ошибка при обновлении профиля');
        this.saving.set(false);
      }
    });
  }

  roleLabel(role?: string): string {
    switch (role) {
      case 'USER': return 'Пользователь';
      case 'EXPERT': return 'Эксперт';
      case 'ADMIN': return 'Администратор';
      default: return role ?? 'Неизвестно';
    }
  }

  roleClass(role?: string): string {
    return role ? 'role-' + role : '';
  }
}
