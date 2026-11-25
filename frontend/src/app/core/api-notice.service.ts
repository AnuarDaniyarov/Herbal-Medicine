import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ApiNoticeService {
  readonly message = signal('');
}
