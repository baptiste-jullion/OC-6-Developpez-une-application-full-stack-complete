import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  TopicListResponse,
  TopicListResponseSchema,
  TopicResponse,
  TopicSubscriptionPathParams,
  TopicSubscriptionPathParamsSchema,
} from '../../models/topic.model';

import { AuthService } from '../auth/auth.service';

@Injectable({ providedIn: 'root' })
export class TopicService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  private readonly topicsSignal = signal<TopicResponse[]>([]);
  private readonly subscribedIdsSignal = signal<Set<string>>(new Set());

  readonly topics = computed(() => this.topicsSignal());
  readonly subscribedIds = computed(() => this.subscribedIdsSignal());

  constructor() {
    effect(() => {
      const user = this.auth.user();
      if (!user) {
        this.subscribedIdsSignal.set(new Set());
      }
    });
  }

  async loadTopics() {
    const raw = await firstValueFrom(
      this.http.get<TopicListResponse>(`${environment.apiUrl}/topics`)
    );
    const topics = TopicListResponseSchema.parse(raw);
    this.topicsSignal.set(topics);

    const subscribedIds = new Set(
      topics.filter((t) => t.subscribed).map((t) => t.id)
    );
    this.subscribedIdsSignal.set(subscribedIds);
    return topics;
  }

  async subscribe({ path }: { path: TopicSubscriptionPathParams }) {
    const { topicId: validatedTopicId } = TopicSubscriptionPathParamsSchema.parse(path);
    await firstValueFrom(
      this.http.post<void>(`${environment.apiUrl}/topics/${validatedTopicId}/subscribe`, {})
    );
    this.updateSubscribed(validatedTopicId, true);
  }

  async unsubscribe({ path }: { path: TopicSubscriptionPathParams }) {
    const { topicId: validatedTopicId } = TopicSubscriptionPathParamsSchema.parse(path);
    await firstValueFrom(
      this.http.delete<void>(`${environment.apiUrl}/topics/${validatedTopicId}/unsubscribe`)
    );
    this.updateSubscribed(validatedTopicId, false);
  }

  isSubscribed(topicId: string): boolean {
    return this.subscribedIdsSignal().has(topicId);
  }

  private updateSubscribed(topicId: string, add: boolean): void {
    const next = new Set(this.subscribedIdsSignal());
    if (add) {
      next.add(topicId);
    } else {
      next.delete(topicId);
    }
    this.subscribedIdsSignal.set(next);
  }
}
