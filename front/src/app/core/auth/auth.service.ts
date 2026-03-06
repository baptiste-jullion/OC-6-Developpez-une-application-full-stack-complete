import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  AuthResponse,
  AuthResponseSchema,
  LoginRequest,
  LoginRequestSchema,
  RegisterRequest,
  RegisterRequestSchema,
} from '../../models/auth.model';
import { UserResponse, UserResponseSchema } from '../../models/user.model';

const TOKEN_KEY = 'mdd.auth.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly tokenSignal = signal<string | null>(this.readToken());
  private readonly userSignal = signal<UserResponse | null>(null);

  readonly token = computed(() => this.tokenSignal());
  readonly user = computed(() => this.userSignal());
  readonly isAuthenticated = computed(() => !!this.tokenSignal());

  async login({ payload }: { payload: LoginRequest }) {
    const validatedCredentials = LoginRequestSchema.parse(payload);
    const raw = await firstValueFrom(
      this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, validatedCredentials)
    );
    const { token } = AuthResponseSchema.parse(raw);

    this.persistToken(token);
    return this.fetchMe();
  }

  async register({ payload }: { payload: RegisterRequest }) {
    const validatedPayload = RegisterRequestSchema.parse(payload);
    const raw = await firstValueFrom(
      this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, validatedPayload)
    );
    const { token } = AuthResponseSchema.parse(raw);

    this.persistToken(token);
    return this.fetchMe();
  }

  async fetchMe() {
    const raw = await firstValueFrom(
      this.http.get<UserResponse>(`${environment.apiUrl}/users/me`)
    );
    const me = UserResponseSchema.parse(raw);

    this.userSignal.set(me);
    return me;
  }

  async restoreSession() {
    if (!this.tokenSignal()) {
      return null;
    }

    try {
      return await this.fetchMe();
    } catch (error) {
      this.logout();
      return null;
    }
  }

  logout(): void {
    this.persistToken(null);
    this.userSignal.set(null);
  }

  setToken(token: string | null): void {
    this.persistToken(token);
  }

  private readToken(): string | null {
    try {
      return localStorage.getItem(TOKEN_KEY);
    } catch {
      return null;
    }
  }

  private persistToken(token: string | null): void {
    try {
      if (token) {
        localStorage.setItem(TOKEN_KEY, token);
      } else {
        localStorage.removeItem(TOKEN_KEY);
      }
    } catch {
    }

    this.tokenSignal.set(token);
  }
}
