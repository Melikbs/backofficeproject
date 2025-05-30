import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { authInterceptor } from './interceptors/auth.interceptor';
import { loadingInterceptor } from './services/loading.interceptor';
import { provideHttpClient, withFetch, withInterceptors, withJsonpSupport } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';


export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor, loadingInterceptor]),
      withJsonpSupport(),
      withFetch() // If using fetch API
    )
  ]
};
