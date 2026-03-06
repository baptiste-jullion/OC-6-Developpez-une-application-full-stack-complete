import z from "zod";

export const AuthResponseSchema = z.object({
  token: z.string(),
})
export type AuthResponse = z.infer<typeof AuthResponseSchema>;

export const RegisterRequestSchema = z.object({
  email: z.email(),
  password: z.string().min(8).max(256).regex(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$/),
  username: z.string().min(3).max(50),
})
export type RegisterRequest = z.infer<typeof RegisterRequestSchema>;

export const LoginRequestSchema = z.object({
  login: z.string(),
  password: z.string(),
})
export type LoginRequest = z.infer<typeof LoginRequestSchema>;