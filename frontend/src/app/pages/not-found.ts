import { Component } from '@angular/core';import { RouterLink } from '@angular/router';
@Component({selector:'app-not-found',standalone:true,imports:[RouterLink],template:'<section class="page"><h1 class="page-title">Страница не найдена</h1><a routerLink="/">На главную</a></section>'})export class NotFoundPage{}
