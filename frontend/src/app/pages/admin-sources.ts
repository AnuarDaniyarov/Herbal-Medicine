import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthApiService } from '../core/auth-api.service';
import { CreateSourceRequest, PageResponse, PlatformStats, Source } from '../core/api.models';

@Component({
  selector: 'app-admin-sources',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-sources.html'
})
export class AdminSourcesPage {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(AuthApiService);
  readonly result = signal<PageResponse<Source> | null>(null);
  readonly stats = signal<PlatformStats | null>(null);
  readonly error = signal('');
  readonly notice = signal('');
  readonly saving = signal(false);
  readonly loading = signal(false);
  readonly statsError = signal('');
  readonly reindexing = signal(false);
  readonly pageIndex = signal(0);

  readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(1000)]],
    authors: ['', Validators.maxLength(1000)],
    publicationYear: [null as number | null, [Validators.min(1500), Validators.max(2100)]],
    doi: ['', Validators.maxLength(300)],
    url: ['', Validators.maxLength(2000)],
    sourceType: ['ARTICLE' as 'ARTICLE' | 'REGISTRY' | 'MONOGRAPH' | 'DATASET' | 'OTHER', Validators.required],
    licenseNote: ['', Validators.maxLength(5000)]
  });

  constructor() {
    this.load();
    this.loadStats();
  }

  load(pageIndex = this.pageIndex()): void {
    this.loading.set(true);
    this.api.adminSources(pageIndex).pipe(finalize(() => this.loading.set(false))).subscribe({
      next: page => { this.result.set(page); this.pageIndex.set(pageIndex); this.error.set(''); },
      error: err => this.error.set(err?.status === 401 || err?.status === 403
        ? 'Управление источниками доступно только администратору.' : 'Не удалось загрузить источники.')
    });
  }

  changePage(pageIndex: number): void {
    if (pageIndex >= 0 && pageIndex !== this.pageIndex()) this.load(pageIndex);
  }

  loadStats(): void {
    this.api.adminStats().subscribe({
      next: s => { this.stats.set(s); this.statsError.set(''); },
      error: () => this.statsError.set('Не удалось загрузить сводную статистику. Проверьте доступ администратора и повторите обновление страницы.')
    });
  }

  reindex(): void {
    this.reindexing.set(true);
    this.notice.set('');
    this.error.set('');
    this.api.adminReindex().pipe(finalize(() => this.reindexing.set(false))).subscribe({
      next: res => {
        if (res.success) this.notice.set(res.message);
        else this.error.set(res.message);
        this.loadStats();
      },
      error: err => {
        this.error.set(err?.error?.message || 'Не удалось переиндексировать эмбеддинги.');
      }
    });
  }

  submit(): void {
    this.notice.set('');
    this.error.set('');
    this.form.markAllAsTouched();
    const value = this.form.getRawValue();
    if (this.form.invalid || (!value.doi?.trim() && !value.url?.trim())) {
      this.error.set('Заполните обязательные поля и добавьте DOI или URL источника.');
      return;
    }
    this.saving.set(true);
    const sourceType = (value.sourceType || 'ARTICLE') as CreateSourceRequest['sourceType'];
    this.api.createSource({
      title: value.title!.trim(),
      authors: value.authors?.trim() || null,
      publicationYear: value.publicationYear || null,
      doi: value.doi?.trim() || null,
      url: value.url?.trim() || null,
      sourceType,
      licenseNote: value.licenseNote?.trim() || null
    })
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: source => {
          this.notice.set(`Источник «${source.title}» добавлен. Его ID: ${source.id}.`);
          this.form.reset({ title: '', authors: '', publicationYear: null, doi: '', url: '', sourceType: 'ARTICLE', licenseNote: '' });
          this.load();
          this.loadStats();
        },
        error: err => this.error.set(err?.error?.message || 'Не удалось сохранить источник.')
      });
  }
}
