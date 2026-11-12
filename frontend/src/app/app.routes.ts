import { Routes } from '@angular/router';
export const routes: Routes = [
 {path:'',loadComponent:()=>import('./pages/home').then(m=>m.HomePage),title:'Herbarium AI — исследовательская платформа'},
 {path:'methodology',loadComponent:()=>import('./pages/methodology').then(m=>m.MethodologyPage),title:'Методология и ограничения'},
 {path:'login',loadComponent:()=>import('./pages/auth').then(m=>m.AuthPage),title:'Вход'},
 {path:'register',loadComponent:()=>import('./pages/auth').then(m=>m.AuthPage),title:'Регистрация'},
 {path:'admin/users',loadComponent:()=>import('./pages/admin-users').then(m=>m.AdminUsersPage),title:'Пользователи и роли'},
 {path:'admin/sources',loadComponent:()=>import('./pages/admin-sources').then(m=>m.AdminSourcesPage),title:'Источники данных'},
 {path:'expert/review',loadComponent:()=>import('./pages/expert-claims').then(m=>m.ExpertClaimsPage),title:'Очередь экспертной проверки'},
 {path:'assistant',loadComponent:()=>import('./pages/assistant').then(m=>m.AssistantPage),title:'Поиск-помощник'},
 {path:'plants',loadComponent:()=>import('./pages/plants').then(m=>m.PlantsPage),title:'Каталог растений'},
 {path:'plants/:id',loadComponent:()=>import('./pages/plant-detail').then(m=>m.PlantDetailPage),title:'Карточка растения'},
 {path:'preparations',loadComponent:()=>import('./pages/preparations').then(m=>m.PreparationsPage),title:'Каталог препаратов'},
 {path:'preparations/:id',loadComponent:()=>import('./pages/preparation-detail').then(m=>m.PreparationDetailPage),title:'Карточка препарата'},
 {path:'favorites',loadComponent:()=>import('./pages/favorites').then(m=>m.FavoritesPage),title:'Избранное'},
 {path:'compounds',loadComponent:()=>import('./pages/compounds').then(m=>m.CompoundsPage),title:'Каталог соединений'},
 {path:'compounds/:id',loadComponent:()=>import('./pages/compound-detail').then(m=>m.CompoundDetailPage),title:'Карточка соединения'},
 {path:'prediction',loadComponent:()=>import('./pages/prediction').then(m=>m.PredictionPage),title:'Demo-оценка'},
 {path:'predictions/:id',loadComponent:()=>import('./pages/prediction-result').then(m=>m.PredictionResultPage),title:'Результат оценки'},
 {path:'comparison',loadComponent:()=>import('./pages/comparison').then(m=>m.ComparisonPage),title:'Сравнение кандидатов'},
 {path:'reports',loadComponent:()=>import('./pages/reports').then(m=>m.ReportsPage),title:'Экспорт кандидатов'},
 {path:'**',loadComponent:()=>import('./pages/not-found').then(m=>m.NotFoundPage),title:'Не найдено'}
];
