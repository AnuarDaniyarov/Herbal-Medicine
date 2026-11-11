import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router'; import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { PageResponse, Plant } from '../core/api.models';
@Component({selector:'app-plants',standalone:true,imports:[RouterLink, FormsModule],templateUrl:'./plants.html'})
export class PlantsPage {
 private api=inject(ApiService); readonly result=signal<PageResponse<Plant>|null>(null); readonly loading=signal(true); readonly error=signal(''); query=''; family=''; page=0;
 constructor(){this.load();}
 load(){this.loading.set(true);this.error.set('');this.api.plants({query:this.query,family:this.family,page:this.page,size:24,sort:'scientificName,asc'}).subscribe({next:r=>{this.result.set(r);this.loading.set(false)},error:e=>{this.error.set(e.error?.message||'Не удалось загрузить каталог растений');this.loading.set(false)}})}
 search(){this.page=0;this.load()} move(n:number){this.page=Math.max(0,Math.min((this.result()?.totalPages||1)-1,this.page+n));this.load()}
}
