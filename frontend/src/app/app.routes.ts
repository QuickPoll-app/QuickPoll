import { Routes } from "@angular/router";
import { LoginComponent } from "./auth/pages/login/login.component";
import { RegisterComponent } from "./auth/pages/register/register.component";
import { MainLayoutComponent } from "./layouts/main-layout/main-layout.component";
import { DashboardComponent } from "./pages/dashboard/dashboard.component";

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
    children: [
      {
        path: "dashboard",
        component: DashboardComponent,
      },
    ],
  },
];
