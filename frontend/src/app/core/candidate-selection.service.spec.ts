import { TestBed } from '@angular/core/testing';
import { CandidateSelectionService } from './candidate-selection.service';
import { CompoundSummary } from './api.models';

const compound: CompoundSummary = {
  id: 42,
  name: 'Demo compound',
  canonicalSmiles: 'CCO',
  structureStatus: 'DEMO',
};

describe('CandidateSelectionService', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [CandidateSelectionService] });
  });

  it('adds and removes comparison candidates', () => {
    const selection = TestBed.inject(CandidateSelectionService);
    selection.toggle(compound);
    expect(selection.ids()).toEqual([42]);
    expect(selection.has(42)).toBe(true);
    selection.toggle(compound);
    expect(selection.count()).toBe(0);
  });
});
