import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthApiService } from './auth-api.service';
import { UserRole } from './api.models';

function roleGuard(allowedRoles?: UserRole[]): CanActivateFn {
  return (_route, state) => {
    const auth = inject(AuthApiService);
    const router = inject(Router);
    const decide = () => {
      const user = auth.currentUser();
      if (!user) {
        return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
      }
      if (allowedRoles && !allowedRoles.includes(user.role)) {
        return router.createUrlTree(['/forbidden']);
      }
      return true;
    };
    return auth.initialized() ? decide() : auth.initialize().pipe(map(decide));
  };
}

export const authenticatedGuard = roleGuard();
export const expertGuard = roleGuard(['EXPERT', 'ADMIN']);
export const adminGuard = roleGuard(['ADMIN']);
