import { CommonModule } from "@angular/common";
import {
  ChangeDetectionStrategy,
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  inject,
  input,
  computed,
} from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { INavItem, IUserProfile } from "../../../models";
import { AuthService } from "../../../services/auth.service";

@Component({
  selector: "app-sidebar",
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: "./sidebar.component.html",
  styleUrl: "./sidebar.component.css",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarComponent {
  public navItems = input.required<INavItem[]>();
  public userProfile = input.required<IUserProfile>();

  private authService = inject(AuthService);

  public filteredNavItems = computed(() => {
    const user = this.authService.getUser();
    const isAdmin = user?.role?.toLowerCase() === 'admin';
    
    return this.navItems().filter(item => {
      if (item.label === 'Create Poll' && !isAdmin) {
        return false;
      }
      if (item.label === 'Users' && !isAdmin) {
        return false;
      }
      return true;
    });
  });

  public userInitials = computed(() => {
    const name = this.userProfile().name;
    const nameParts = name.trim().split(' ').filter(part => part.length > 0);
    
    if (nameParts.length === 1) {
      return nameParts[0][0].toUpperCase();
    }
    
    return (nameParts[0][0] + nameParts[nameParts.length - 1][0]).toUpperCase();
  });

  public onLogout(): void {
    this.authService.logout();
  }
}

export { INavItem, IUserProfile };
