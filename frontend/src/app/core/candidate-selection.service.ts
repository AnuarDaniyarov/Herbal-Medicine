import { Injectable, computed, signal } from '@angular/core';
import { CompoundSummary } from './api.models';

const STORAGE_KEY = 'medicine-platform.compare-candidate-ids';

@Injectable({ providedIn: 'root' })
export class CandidateSelectionService {
  private readonly idsState = signal<number[]>(this.restore());
  readonly ids = this.idsState.asReadonly();
  readonly count = computed(() => this.idsState().length);

  has(id: number): boolean {
    return this.idsState().includes(id);
  }

  toggle(compound: CompoundSummary): void {
    const current = this.idsState();
    const next = current.includes(compound.id)
      ? current.filter((id) => id !== compound.id)
      : current.length >= 20
        ? current
        : [...current, compound.id];
    this.idsState.set(next);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
  }

  clear(): void {
    this.idsState.set([]);
    localStorage.removeItem(STORAGE_KEY);
  }

  private restore(): number[] {
    try {
      const parsed: unknown = JSON.parse(localStorage.getItem(STORAGE_KEY) ?? '[]');
      return Array.isArray(parsed)
        ? parsed.filter((id): id is number => Number.isInteger(id) && id > 0).slice(0, 20)
        : [];
    } catch {
      return [];
    }
  }
}
