import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../core/api.service';
import { Assay } from '../core/api.models';

const ACHE_ACTIVITY = 'imppat_ic50_e662faf14ac2';

@Component({ selector: 'app-prediction', standalone: true, imports: [ReactiveFormsModule], templateUrl: './prediction.html' })
export class PredictionPage {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  readonly assays = signal<Assay[]>([]);
  readonly error = signal('');
  readonly loading = signal(false);
  readonly form = inject(FormBuilder).nonNullable.group({
    smiles: ['', [Validators.required, Validators.maxLength(2000)]],
    activityCode: ['demo', Validators.required],
  });

  constructor() {
    const compoundId = Number(inject(ActivatedRoute).snapshot.queryParamMap.get('compoundId'));
    this.api.assays().subscribe({
      next: (assays) => {
        const supported = assays.filter((assay) => assay.activityCode === 'demo' || assay.activityCode === ACHE_ACTIVITY);
        this.assays.set(supported);
        this.form.controls.activityCode.setValue(
          supported.some((assay) => assay.activityCode === ACHE_ACTIVITY) ? ACHE_ACTIVITY : 'demo',
        );
      },
      error: () => this.error.set('Не удалось загрузить список доступных типов оценки.'),
    });
    if (compoundId > 0) {
      this.api.compound(compoundId).subscribe((compound) => this.form.controls.smiles.setValue(compound.canonicalSmiles));
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.api.predict(this.form.getRawValue()).subscribe({
      next: (result) => this.router.navigate(['/predictions', result.predictionId]),
      error: (error) => {
        this.error.set(error.error?.message || 'Не удалось получить оценку.');
        this.loading.set(false);
      },
    });
  }
}
