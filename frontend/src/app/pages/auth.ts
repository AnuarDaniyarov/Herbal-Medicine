import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthApiService } from '../core/auth-api.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './auth.html'
})
export class AuthPage {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly registerMode = signal(false);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly notice = signal('');
  readonly form = this.fb.group({
    displayName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(200)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(320)]],
    password: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(128)]]
  });

  constructor() {
    this.registerMode.set(this.route.snapshot.url.some(segment => segment.path === 'register'));
  }

  submit(): void {
    this.error.set('');
    this.notice.set('');
    const { displayName, email, password } = this.form.getRawValue();
    if (!email || !password || (this.registerMode() && !displayName)) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    const request = this.registerMode()
      ? this.auth.register({ email, displayName: displayName!, password })
      : this.auth.login({ email, password });
    request.pipe(finalize(() => this.loading.set(false))).subscribe({
      next: () => {
        if (this.registerMode()) {
          this.notice.set('Аккаунт создан. Теперь войдите в систему.');
          this.form.reset();
        } else {
          void this.router.navigateByUrl('/');
        }
      },
      error: error => this.error.set(error?.error?.message || 'Не удалось выполнить запрос. Проверьте данные и повторите попытку.')
    });
  }
}
