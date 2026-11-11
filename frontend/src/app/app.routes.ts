import { Routes } from '@angular/router';
export const routes: Routes = [
 {path:'',loadComponent:()=>import('./pages/home').then(m=>m.HomePage),title:'Herbarium AI — исследовательская платформа'},
 {path:'methodology',loadComponent:()=>import('./pages/methodology').then(m=>m.MethodologyPage),title:'Методология и ограничения'},
 {path:'plants',loadComponent:()=>import('./pages/plants').then(m=>m.PlantsPage),title:'Каталог растений'},
 {path:'plants/:id',loadComponent:()=>import('./pages/plant-detail').then(m=>m.PlantDetailPage),title:'Карточка растения'},
 {path:'compounds',loadComponent:()=>import('./pages/compounds').then(m=>m.CompoundsPage),title:'Каталог соединений'},
 {path:'compounds/:id',loadComponent:()=>import('./pages/compound-detail').then(m=>m.CompoundDetailPage),title:'Карточка соединения'},
 {path:'prediction',loadComponent:()=>import('./pages/prediction').then(m=>m.PredictionPage),title:'Demo-оценка'},
 {path:'predictions/:id',loadComponent:()=>import('./pages/prediction-result').then(m=>m.PredictionResultPage),title:'Результат оценки'},
 {path:'comparison',loadComponent:()=>import('./pages/comparison').then(m=>m.ComparisonPage),title:'Сравнение кандидатов'},
 {path:'reports',loadComponent:()=>import('./pages/reports').then(m=>m.ReportsPage),title:'Экспорт кандидатов'},
 {path:'**',loadComponent:()=>import('./pages/not-found').then(m=>m.NotFoundPage),title:'Не найдено'}
];
