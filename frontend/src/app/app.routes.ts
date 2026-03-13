import { Routes } from "@angular/router";
import { LoginComponent } from "./auth/pages/login/login.component";
import { RegisterComponent } from "./auth/pages/register/register.component";
import { MainLayoutComponent } from "./layouts/main-layout/main-layout.component";
import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { CreatePollComponent } from "./pages/create-poll/create-poll.component";
import { EditPollComponent } from "./pages/edit-poll/edit-poll.component";
import { PollsListComponent } from "./pages/polls-list/polls-list.component";
import { PollVoteMultiComponent } from "./pages/poll-vote-multi/poll-vote-multi.component";
import { PollVoteSingleComponent } from "./pages/poll-vote-single/poll-vote-single.component";
import { PollExpiredComponent } from "./pages/poll-expired/poll-expired.component";
import { PollResultsComponent } from "./pages/poll-results/poll-results.component";
import { UserManagementComponent } from "./pages/user-management/user-management.component";
import { SettingsComponent } from "./pages/settings/settings.component";
import { NotFoundComponent } from "./pages/not-found/not-found.component";
import { authGuard } from "./guards/auth.guard";

export const routes: Routes = [
  {
    path: "",
    redirectTo: "login",
    pathMatch: "full",
  },
  {
    path: "login",
    component: LoginComponent,
  },
  {
    path: "registration",
    component: RegisterComponent,
  },
  {
    path: "",
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: "dashboard",
        component: DashboardComponent,
      },
      {
        path: "polls",
        component: PollsListComponent,
      },
      {
        path: "create-poll",
        component: CreatePollComponent,
      },
      {
        path: "edit-poll/:id",
        component: EditPollComponent,
      },
      {
        path: "users",
        component: UserManagementComponent,
      },
      {
        path: "settings",
        component: SettingsComponent,
      },
      {
        path: "poll/:id/vote-single",
        component: PollVoteSingleComponent,
      },
      {
        path: "poll/:id/vote-multi",
        component: PollVoteMultiComponent,
      },
      {
        path: "poll/:id/results",
        component: PollResultsComponent,
      },
      {
        path: "poll/:id/expired",
        component: PollExpiredComponent,
      },
    ],
  },
  {
    path: "**",
    component: NotFoundComponent,
  },
];
