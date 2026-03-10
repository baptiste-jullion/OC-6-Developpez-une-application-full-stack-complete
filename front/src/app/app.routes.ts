import { Routes } from '@angular/router';

import { authGuard } from './core/auth/auth.guard';
import { publicGuard } from './core/auth/public.guard';

export const routes: Routes = [
	{
		path: '',
		pathMatch: 'full',
		canActivate: [publicGuard],
		loadComponent: () =>
			import('./features/landing/landing.component').then((m) => m.LandingComponent),
	},
	{
		path: '',
		loadComponent: () => import('./shell/shell.component').then((m) => m.ShellComponent),
		children: [
			{
				path: 'login',
				canActivate: [publicGuard],
				loadComponent: () =>
					import('./features/auth/login/login.component').then((m) => m.LoginComponent),
			},
			{
				path: 'register',
				canActivate: [publicGuard],
				loadComponent: () =>
					import('./features/auth/register/register.component').then(
						(m) => m.RegisterComponent
					),
			},
			{
				path: 'posts',
				canActivate: [authGuard],
				loadComponent: () =>
					import('./features/posts/post-list/post-list.component').then(
						(m) => m.PostListComponent
					),
			},
			{
				path: 'posts/create',
				canActivate: [authGuard],
				loadComponent: () =>
					import('./features/posts/post-create/post-create.component').then(
						(m) => m.PostCreateComponent
					),
			},
			{
				path: 'posts/:postId',
				canActivate: [authGuard],
				loadComponent: () =>
					import('./features/posts/post-detail/post-detail.component').then(
						(m) => m.PostDetailComponent
					),
			},
			{
				path: 'topics',
				canActivate: [authGuard],
				loadComponent: () =>
					import('./features/topics/topic-list/topic-list.component').then(
						(m) => m.TopicListComponent
					),
			},
			{
				path: 'profile',
				canActivate: [authGuard],
				loadComponent: () =>
					import('./features/profile/profile.component').then((m) => m.ProfileComponent),
			},
		],
	},
	{ path: '**', redirectTo: '' },
];
