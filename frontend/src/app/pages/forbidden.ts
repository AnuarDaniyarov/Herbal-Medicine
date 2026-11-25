import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [RouterLink],
  template: '<section class="page"><div class="error" role="alert">Недостаточно прав для просмотра этой страницы.</div><a routerLink="/">Вернуться на главную</a></section>'
})
export class ForbiddenPage {}
