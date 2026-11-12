import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AssistantApiService } from '../core/assistant-api.service';
import { AssistantHistoryEntry, AssistantResponse } from '../core/api.models';
import { AuthApiService } from '../core/auth-api.service';

@Component({
  selector: 'app-assistant',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './assistant.html'
})
export class AssistantPage {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(AssistantApiService);
  private readonly auth = inject(AuthApiService);
  readonly account = this.auth.currentUser;
  readonly loading = signal(false);
  readonly error = signal('');
  readonly conversationId = signal<number | undefined>(undefined);
  readonly responses = signal<{ question: string; response: AssistantResponse }[]>([]);
  readonly history = signal<AssistantHistoryEntry[]>([]);
  readonly form = this.fb.group({ question: ['', [Validators.required, Validators.maxLength(2000)]] });

  constructor() {
    if (this.auth.currentUser()) this.loadHistory();
  }

  loadHistory(): void {
    this.api.history().subscribe({ next: page => this.history.set(page.content), error: () => this.history.set([]) });
  }

  ask(): void {
    this.error.set('');
    if (this.form.invalid || this.loading()) { this.form.markAllAsTouched(); return; }
    const question = this.form.controls.question.value?.trim();
    if (!question) return;
    this.loading.set(true);
    this.api.ask({ question, ...(this.conversationId() ? { conversationId: this.conversationId() } : {}) }).subscribe({
      next: response => {
        this.conversationId.set(response.conversationId);
        this.responses.update(items => [...items, { question, response }]);
        this.history.update(items => [{ conversationId: response.conversationId, updatedAt: new Date().toISOString(), lastQuestion: question, lastAnswer: response.answer, lastStatus: response.status }, ...items.filter(item => item.conversationId !== response.conversationId)]);
        this.form.reset();
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err?.status === 401 ? 'Чтобы пользоваться помощником и сохранять историю, войдите в аккаунт.'
          : err?.error?.message || 'Запрос не выполнен. Проверьте подключение и повторите попытку.');
        this.loading.set(false);
      }
    });
  }
}
