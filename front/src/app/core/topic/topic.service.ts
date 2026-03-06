import { Injectable, computed, inject, signal } from '@angular/core';
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

const SUBS_KEY = 'mdd.topic.subscriptions';

@Injectable({ providedIn: 'root' })
export class TopicService {
  private readonly http = inject(HttpClient);

  private readonly topicsSignal = signal<TopicResponse[]>([]);
  private readonly subscribedIdsSignal = signal<Set<string>>(this.restoreSubscriptions());

  readonly topics = computed(() => this.topicsSignal());
  readonly subscribedIds = computed(() => this.subscribedIdsSignal());

  async loadTopics() {
    const raw = await firstValueFrom(
      this.http.get<TopicListResponse>(`${environment.apiUrl}/topics`)
    );
    const topics = TopicListResponseSchema.parse(raw);
    this.topicsSignal.set(topics);
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
    this.persistSubscriptions(next);
  }

  private restoreSubscriptions(): Set<string> {
    try {
      const raw = localStorage.getItem(SUBS_KEY);
      if (!raw) return new Set();
      const parsed: string[] = JSON.parse(raw);
      return new Set(parsed);
    } catch {
      return new Set();
    }
  }

  private persistSubscriptions(ids: Set<string>): void {
    try {
      localStorage.setItem(SUBS_KEY, JSON.stringify(Array.from(ids)));
    } catch {}
  }
}
