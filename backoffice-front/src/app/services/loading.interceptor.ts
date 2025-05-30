import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoadingService } from './loading.service'; // Ensure this service is correctly implemented
import { finalize } from 'rxjs/operators';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingService = inject(LoadingService);
  loadingService.show(); // Start loading

  return next(req).pipe(
    finalize(() => loadingService.hide()) // Hide loading after request is completed
  );
};
