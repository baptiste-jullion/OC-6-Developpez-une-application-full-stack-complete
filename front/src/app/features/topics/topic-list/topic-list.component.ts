import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

import { TopicService } from '../../../core/topic/topic.service';

@Component({
  selector: 'app-topic-list',
  standalone: true,
  imports: [MatCardModule, MatButtonModule],
  templateUrl: './topic-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicListComponent {
  private readonly topicService = inject(TopicService);

  protected readonly topics = this.topicService.topics;

  constructor() {
    void this.topicService.loadTopics();
  }

  protected isSubscribed(topicId: string): boolean {
    return this.topicService.isSubscribed(topicId);
  }

  protected async subscribe(topicId: string): Promise<void> {
    await this.topicService.subscribe({ path: { topicId } });
  }
}
