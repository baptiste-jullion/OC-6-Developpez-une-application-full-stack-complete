import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { AuthService } from '../../core/auth/auth.service';
import { TopicService } from '../../core/topic/topic.service';
import { UserService } from '../../core/user/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './profile.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileComponent {
  private readonly auth = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly topicService = inject(TopicService);
  private readonly fb = inject(FormBuilder);

  protected readonly isSubmitting = signal(false);
  protected readonly me = this.auth.user;
  protected readonly topics = this.topicService.topics;

  protected readonly subscribedTopics = computed(() =>
    this.topics().filter((t) => this.topicService.isSubscribed(t.id))
  );

  protected readonly form = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  constructor() {
    void this.topicService.loadTopics();

    effect(() => {
      const me = this.me();
      if (!me) return;

      this.form.patchValue({
        username: me.username,
        email: me.email,
      }, { emitEvent: false });
    });
  }

  async updateProfile(): Promise<void> {
    if (this.form.invalid || this.isSubmitting()) return;
    this.isSubmitting.set(true);
    try {
      await this.userService.updateProfile({ payload: this.form.getRawValue() });
    } finally {
      this.isSubmitting.set(false);
    }
  }

  protected async unsubscribe(topicId: string): Promise<void> {
    await this.topicService.unsubscribe({ path: { topicId } });
  }
}
