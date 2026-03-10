import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';

import { PostService } from '../../../core/post/post.service';

type SortOrder = 'date_desc' | 'date_asc';

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [RouterLink, DatePipe, MatButtonModule, MatCardModule, MatFormFieldModule, MatSelectModule],
  templateUrl: './post-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostListComponent {
  private readonly postService = inject(PostService);

  protected readonly sortOrder = signal<SortOrder>('date_desc');
  protected readonly feed = this.postService.feed;

  protected readonly sortedFeed = computed(() => {
    const items = [...this.feed()];
    const direction = this.sortOrder() === 'date_asc' ? 1 : -1;
    items.sort((a, b) => {
      const aTime = a.createdAt?.getTime?.() ?? 0;
      const bTime = b.createdAt?.getTime?.() ?? 0;
      return (aTime - bTime) * direction;
    });
    return items;
  });

  constructor() {
    void this.postService.loadFeed();
  }
}
