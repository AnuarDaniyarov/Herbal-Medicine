import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CandidateSelectionService } from './core/candidate-selection.service';
import { AuthApiService } from './core/auth-api.service';
import { ApiNoticeService } from './core/api-notice.service';

@Component({ selector: 'app-root', imports: [RouterLink, RouterLinkActive, RouterOutlet], templateUrl: './app.html', styleUrl: './app.scss' })
export class App {
  readonly selection = inject(CandidateSelectionService);
  readonly auth = inject(AuthApiService);
  readonly notice = inject(ApiNoticeService);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout().subscribe({ next: () => void this.router.navigateByUrl('/') });
  }
}
