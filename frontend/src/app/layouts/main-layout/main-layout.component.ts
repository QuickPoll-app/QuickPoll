import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
  CUSTOM_ELEMENTS_SCHEMA,
} from "@angular/core";
import { RouterOutlet, Router } from "@angular/router";
import { SidebarComponent } from "../../shared/components/sidebar/sidebar.component";
import { MobileNavButtonComponent } from "../../shared/components/mobile-nav-button/mobile-nav-button.component";
import { INavItem, IUserProfile } from "../../models";
import { AuthService } from "../../services/auth.service";
import { ThemeService } from "../../services/theme.service";

@Component({
  selector: "app-main-layout",
  imports: [RouterOutlet, SidebarComponent, MobileNavButtonComponent],
  templateUrl: "./main-layout.component.html",
  styleUrl: "./main-layout.component.css",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MainLayoutComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private themeService = inject(ThemeService);

  public isSidebarOpen = signal(false);
  public isDarkMode = this.themeService.isDarkMode;

  public navItems: INavItem[] = [
    {
      label: "Dashboard",
      icon: "lucide:layout-dashboard",
      route: "/dashboard",
    },
    { label: "Polls", icon: "lucide:vote", route: "/polls" },
    { label: "Create Poll", icon: "lucide:plus-circle", route: "/create-poll" },
    { label: "Users", icon: "lucide:users", route: "/users" },
    { label: "Settings", icon: "lucide:settings", route: "/settings" },
  ];

  public userProfile: IUserProfile = (() => {
    const user = this.authService.getUser();

    return {
      name: user?.name.toLocaleUpperCase() || "Guest User",
      role: user?.role || "User",
    };
  })();

  public isActiveRoute(route: string): boolean {
    return this.router.url === route;
  }

  public navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  public toggleSidebar(): void {
    this.isSidebarOpen.update(v => !v);
  }

  public onToggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
