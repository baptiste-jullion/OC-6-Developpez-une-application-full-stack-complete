import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { filter, map } from 'rxjs';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';

import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
  ],
  templateUrl: './shell.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShellComponent {
  protected readonly auth = inject(AuthService);

  private readonly router = inject(Router);
  protected readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => event.urlAfterRedirects)
    ),
    { initialValue: this.router.url }
  );

  protected readonly isAuthRoute = computed(() => {
    const url = this.currentUrl();
    return url.startsWith('/login') || url.startsWith('/register');
  });

  protected disconnect(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/');
  }
}
