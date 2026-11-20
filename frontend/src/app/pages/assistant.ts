import { Component, effect, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AssistantApiService } from '../core/assistant-api.service';
import { AssistantHistoryEntry, AssistantHistoryMessage, AssistantResponse } from '../core/api.models';
import { AuthApiService } from '../core/auth-api.service';

@Component({
  selector: 'app-assistant',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './assistant.html'
})
export class AssistantPage implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(AssistantApiService);
  private readonly auth = inject(AuthApiService);
  private readonly route = inject(ActivatedRoute);

  readonly account = this.auth.currentUser;
  readonly loading = signal(false);
  readonly error = signal('');
  readonly historyError = signal('');
  readonly conversationId = signal<number | undefined>(undefined);
  private readonly requestedConversationId = signal<number | null>(null);
  readonly responses = signal<{ question: string; response: AssistantResponse }[]>([]);
  readonly history = signal<AssistantHistoryEntry[]>([]);
  readonly selectedConversation = signal<AssistantHistoryMessage[]>([]);
  readonly conversationLoading = signal(false);
  readonly conversationError = signal('');
  readonly form = this.fb.group({ question: ['', [Validators.required, Validators.maxLength(2000)]] });

  private readonly authHistoryEffect = effect(() => {
    if (!this.auth.initialized()) return;
    if (!this.auth.currentUser()) {
      this.history.set([]);
      if (this.requestedConversationId()) {
        this.conversationError.set('Войдите в аккаунт, чтобы открыть сохранённую переписку.');
      }
      return;
    }
    this.loadHistory();
    const conversationId = this.requestedConversationId();
    if (conversationId) this.loadConversation(conversationId);
  });

  readonly quickPrompts = [
    'Можно ли принимать валериану вместе с антидепрессантами?',
    'Какие есть противопоказания у зверобоя?',
    'Что из проверенных трав обладает отхаркивающим действием?',
    'Какие показания к применению ромашки аптечной?'
  ];

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const q = params['q'];
      const conversationId = Number(params['conversationId']);
      if (Number.isSafeInteger(conversationId) && conversationId > 0) {
        this.requestedConversationId.set(conversationId);
      } else if (q) {
        this.form.controls.question.setValue(q);
      }
    });
  }

  loadHistory(): void {
    this.api.history().subscribe({
      next: page => {
        this.history.set(page.content);
        this.historyError.set('');
      },
      error: err => this.historyError.set(err?.error?.message || 'Не удалось загрузить историю диалогов.')
    });
  }

  ask(): void {
    this.error.set('');
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsTouched();
      return;
    }
    const question = this.form.controls.question.value?.trim();
    if (!question) return;
    this.loading.set(true);
    this.api.ask({
      question,
      ...(this.conversationId() ? { conversationId: this.conversationId() } : {})
    }).subscribe({
      next: response => {
        this.conversationId.set(response.conversationId);
        this.responses.update(items => [...items, { question, response }]);
        this.history.update(items => [
          {
            conversationId: response.conversationId,
            updatedAt: new Date().toISOString(),
            lastQuestion: question,
            lastAnswer: response.answer,
            lastStatus: response.status
          },
          ...items.filter(item => item.conversationId !== response.conversationId)
        ]);
        this.historyError.set('');
        this.form.reset();
        this.loading.set(false);
      },
      error: err => {
        this.error.set(err?.status === 401
          ? 'Чтобы пользоваться помощником и сохранять историю, войдите в аккаунт.'
          : err?.error?.message || 'Запрос не выполнен. Проверьте подключение и повторите попытку.');
        this.loading.set(false);
      }
    });
  }

  selectHistory(entry: AssistantHistoryEntry): void {
    this.loadConversation(entry.conversationId);
  }

  loadConversation(conversationId: number): void {
    this.requestedConversationId.set(null);
    this.conversationId.set(conversationId);
    this.selectedConversation.set([]);
    this.responses.set([]);
    this.conversationLoading.set(true);
    this.conversationError.set('');
    this.api.conversation(conversationId).subscribe({
      next: messages => {
        this.selectedConversation.set(messages);
        this.conversationLoading.set(false);
      },
      error: err => {
        this.conversationId.set(undefined);
        this.conversationError.set(err?.error?.message || 'Не удалось загрузить сообщения диалога.');
        this.conversationLoading.set(false);
      }
    });
  }

  useQuickPrompt(prompt: string): void {
    this.form.controls.question.setValue(prompt);
    this.ask();
  }

  startNewConversation(): void {
    this.requestedConversationId.set(null);
    this.conversationId.set(undefined);
    this.selectedConversation.set([]);
    this.conversationError.set('');
    this.responses.set([]);
    this.form.reset();
    this.error.set('');
  }
}
