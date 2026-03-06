import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../environments/environment';
import { AuthResponseSchema } from '../../models/auth.model';
import {
  UserUpdateRequest,
  UserUpdateRequestSchema,
  UserUpdateResponse
} from '../../models/user.model';
import { AuthService } from '../auth/auth.service';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  async getMe() {
    return this.auth.fetchMe();
  }

  async updateProfile({ payload }: { payload: UserUpdateRequest }) {
    const validatedPayload = UserUpdateRequestSchema.parse(payload);
    const raw = await firstValueFrom(
      this.http.put<UserUpdateResponse>(`${environment.apiUrl}/users/me`, validatedPayload)
    );
    const { token } = AuthResponseSchema.parse(raw);
    this.auth.setToken(token);
    return this.auth.fetchMe();
  }
}
