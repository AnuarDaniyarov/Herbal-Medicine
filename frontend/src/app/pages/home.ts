import { Component, inject, signal } from '@angular/core';import { RouterLink } from '@angular/router';import { ApiService } from '../core/api.service';
@Component({selector:'app-home',standalone:true,imports:[RouterLink],templateUrl:'./home.html'})
export class HomePage {readonly plants=signal<number|null>(null);readonly compounds=signal<number|null>(null);constructor(){const api=inject(ApiService);api.plants({page:0,size:1}).subscribe(v=>this.plants.set(v.totalElements));api.compounds({page:0,size:1}).subscribe(v=>this.compounds.set(v.totalElements));}}
