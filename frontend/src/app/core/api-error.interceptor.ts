import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthApiService } from './auth-api.service';
import { ApiNoticeService } from './api-notice.service';

export const apiErrorInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthApiService);
  const router = inject(Router);
  const notice = inject(ApiNoticeService);
  return next(request).pipe(catchError((error: unknown) => {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401 && !request.url.includes('/api/auth/')) {
        auth.currentUser.set(null);
        void router.navigate(['/login'], { queryParams: { returnUrl: router.url } });
      } else if (error.status === 403) {
        notice.message.set('Недостаточно прав для выполнения действия.');
      } else if (error.status === 429) {
        const retryAfter = Number(error.headers.get('Retry-After'));
        const seconds = Number.isFinite(retryAfter) && retryAfter > 0 ? Math.ceil(retryAfter) : null;
        notice.message.set(seconds === null
          ? 'Слишком много запросов. Повторите попытку позже.'
          : `Слишком много запросов, повторите через ${seconds} с.`);
      }
    }
    return throwError(() => error);
  }));
};
