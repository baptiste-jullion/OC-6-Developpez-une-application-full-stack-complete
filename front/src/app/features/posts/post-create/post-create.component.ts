import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { PostService } from '../../../core/post/post.service';
import { TopicService } from '../../../core/topic/topic.service';

@Component({
  selector: 'app-post-create',
  standalone: true,
  imports: [
    RouterLink,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './post-create.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostCreateComponent {
  private readonly postService = inject(PostService);
  private readonly topicService = inject(TopicService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly isSubmitting = signal(false);
  protected readonly topics = this.topicService.topics;

  protected readonly form = this.fb.nonNullable.group({
    topicId: ['', [Validators.required]],
    title: ['', [Validators.required]],
    content: ['', [Validators.required]],
  });

  constructor() {
    void this.topicService.loadTopics();
  }

  async submit(): Promise<void> {
    if (this.form.invalid || this.isSubmitting()) return;
    this.isSubmitting.set(true);
    try {
      await this.postService.createPost({ payload: this.form.getRawValue() });
      await this.postService.loadFeed();
      await this.router.navigateByUrl('/posts');
    } finally {
      this.isSubmitting.set(false);
    }
  }
}
