import z from "zod";

export const CommentResponseSchema = z.object({
  author: z.string(),
  content: z.string(),
  createdAt: z.coerce.date(),
  id: z.uuid(),
})
export type CommentResponse = z.infer<typeof CommentResponseSchema>;

export const CommentListResponseSchema = z.array(CommentResponseSchema);
export type CommentListResponse = z.infer<typeof CommentListResponseSchema>;

export const PostResponseSchema = z.object({
  author: z.string(),
  comments: z.array(CommentResponseSchema).nullable().default([]),
  content: z.string(),
  createdAt: z.coerce.date(),
  id: z.uuid(),
  title: z.string(),
  topic: z.string(),
})
export type PostResponse = z.infer<typeof PostResponseSchema>;

export const PostListResponseSchema = z.array(PostResponseSchema);
export type PostListResponse = z.infer<typeof PostListResponseSchema>;

export const CreatePostRequestSchema = z.object({
  content: z.string(),
  title: z.string(),
  topicId: z.uuid(),
})
export type CreatePostRequest = z.infer<typeof CreatePostRequestSchema>;

export const AddCommentRequestSchema = z.object({
  content: z.string(),
})
export type AddCommentRequest = z.infer<typeof AddCommentRequestSchema>;

export const AddCommentPathParamsSchema = z.object({
  postId: z.uuid(),
})
export type AddCommentPathParams = z.infer<typeof AddCommentPathParamsSchema>;

export const GetPostPathParamsSchema = z.object({
  postId: z.uuid(),
})
export type GetPostPathParams = z.infer<typeof GetPostPathParamsSchema>;

export const FeedResponseSchema = PostListResponseSchema;
export type FeedResponse = z.infer<typeof FeedResponseSchema>;