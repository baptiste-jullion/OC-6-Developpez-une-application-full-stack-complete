import z from "zod";

export const TopicResponseSchema = z.object({
  id: z.uuid(),
  title: z.string(),
  description: z.string(),
})
export type TopicResponse = z.infer<typeof TopicResponseSchema>;

export const TopicListResponseSchema = z.array(TopicResponseSchema);
export type TopicListResponse = z.infer<typeof TopicListResponseSchema>;

export const TopicSubscriptionPathParamsSchema = z.object({
  topicId: z.uuid(),
})
export type TopicSubscriptionPathParams = z.infer<typeof TopicSubscriptionPathParamsSchema>;