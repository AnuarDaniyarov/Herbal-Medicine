import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthApiService } from '../core/auth-api.service';
import { CreateSourceRequest, PageResponse, Source } from '../core/api.models';

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
  readonly error = signal('');
  readonly notice = signal('');
  readonly saving = signal(false);
  readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(1000)]],
    authors: ['', Validators.maxLength(1000)],
    publicationYear: [null as number | null, [Validators.min(1500), Validators.max(2100)]],
    doi: ['', Validators.maxLength(300)],
    url: ['', Validators.maxLength(2000)],
    sourceType: ['ARTICLE' as 'ARTICLE' | 'REGISTRY' | 'MONOGRAPH' | 'DATASET' | 'OTHER', Validators.required],
    licenseNote: ['', Validators.maxLength(5000)]
  });

  constructor() { this.load(); }

  load(): void {
    this.api.adminSources().subscribe({
      next: page => { this.result.set(page); this.error.set(''); },
      error: err => this.error.set(err?.status === 401 || err?.status === 403
        ? 'Управление источниками доступно только администратору.' : 'Не удалось загрузить источники.')
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
        },
        error: err => this.error.set(err?.error?.message || 'Не удалось сохранить источник.')
      });
  }
}
