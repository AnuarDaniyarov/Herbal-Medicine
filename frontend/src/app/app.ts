import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CandidateSelectionService } from './core/candidate-selection.service';
import { AuthApiService } from './core/auth-api.service';

@Component({ selector: 'app-root', imports: [RouterLink, RouterLinkActive, RouterOutlet], templateUrl: './app.html', styleUrl: './app.scss' })
export class App implements OnInit {
  readonly selection = inject(CandidateSelectionService);
  readonly auth = inject(AuthApiService);
  private readonly router = inject(Router);

  ngOnInit(): void { this.auth.initialize(); }

  logout(): void {
    this.auth.logout().subscribe({ next: () => void this.router.navigateByUrl('/') });
  }
}
