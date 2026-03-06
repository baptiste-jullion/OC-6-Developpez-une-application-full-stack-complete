import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class App {
  private readonly auth = inject(AuthService);

  constructor() {
    this.auth.restoreSession();
  }
}
