import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  AddCommentPathParamsSchema,
  AddCommentPathParams,
  AddCommentRequest,
  AddCommentRequestSchema, CommentResponseSchema,
  CreatePostRequest,
  CreatePostRequestSchema, PostResponse,
  PostResponseSchema,
  FeedResponse,
  FeedResponseSchema,
  GetPostPathParams,
  GetPostPathParamsSchema,
  CommentResponse
} from '../../models/post.model';

@Injectable({ providedIn: 'root' })
export class PostService {
  private readonly http = inject(HttpClient);

  private readonly feedSignal = signal<PostResponse[]>([]);

  readonly feed = computed(() => this.feedSignal());

  async loadFeed() {
    const raw = await firstValueFrom(
      this.http.get<FeedResponse>(`${environment.apiUrl}/posts/feed`)
    );
    const feed = FeedResponseSchema.parse(raw);
    this.feedSignal.set(feed);
    return feed;
  }

  async getPostById({ path }: { path: GetPostPathParams }) {
    const { postId } = GetPostPathParamsSchema.parse(path);
    const raw = await firstValueFrom(
      this.http.get<PostResponse>(`${environment.apiUrl}/posts/${postId}`)
    );
    return PostResponseSchema.parse(raw);
  }

  async createPost({ payload }: { payload: CreatePostRequest }) {
    const validatedPayload = CreatePostRequestSchema.parse(payload);
    const raw = await firstValueFrom(
      this.http.post<PostResponse>(`${environment.apiUrl}/posts`, validatedPayload)
    );
    return PostResponseSchema.parse(raw);
  }

  async addComment({
    path,
    payload,
  }: {
    path: AddCommentPathParams;
    payload: AddCommentRequest;
  }) {
    const validatedParams = AddCommentPathParamsSchema.parse(path);
    const validatedPayload = AddCommentRequestSchema.parse(payload);
    const raw = await firstValueFrom(
      this.http.post<CommentResponse>(
        `${environment.apiUrl}/posts/${validatedParams.postId}/comments`,
        validatedPayload
      )
    );
    return CommentResponseSchema.parse(raw);
  }
}
