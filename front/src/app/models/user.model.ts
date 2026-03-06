import z from "zod";
import { AuthResponseSchema, RegisterRequestSchema } from "./auth.model";

export const UserResponseSchema = z.object({
  id: z.uuid(),
  username: z.string(),
  email: z.email(),
});
export type UserResponse = z.infer<typeof UserResponseSchema>;

export const UserUpdateRequestSchema = RegisterRequestSchema;
export type UserUpdateRequest = z.infer<typeof UserUpdateRequestSchema>;

export const UserUpdateResponseSchema = AuthResponseSchema;
export type UserUpdateResponse = z.infer<typeof UserUpdateResponseSchema>;