import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { PostService } from '../../../core/post/post.service';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [
    DatePipe,
    RouterLink,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './post-detail.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly postService = inject(PostService);
  private readonly fb = inject(FormBuilder);

  protected readonly post = this.postService.selectedPost;
  protected readonly isSending = signal(false);

  protected readonly commentForm = this.fb.nonNullable.group({
    content: ['', [Validators.required]],
  });

  constructor() {
    const postId = this.route.snapshot.paramMap.get('postId');
    if (postId) {
      void this.postService.loadPostById({ path: { postId } });
    }
  }

  async sendComment(): Promise<void> {
    const post = this.post();
    if (!post) return;
    if (this.commentForm.invalid || this.isSending()) return;

    this.isSending.set(true);
    try {
      await this.postService.addComment({
        path: { postId: post.id },
        payload: this.commentForm.getRawValue(),
      });
      this.commentForm.reset({ content: '' });
    } finally {
      this.isSending.set(false);
    }
  }
}
