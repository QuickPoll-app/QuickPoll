import { CommonModule } from "@angular/common";
import { ChangeDetectionStrategy, Component, CUSTOM_ELEMENTS_SCHEMA, input } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { INavItem, IUserProfile } from "../../../models";

@Component({
  selector: "app-sidebar",
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: "./sidebar.component.html",
  styleUrl: "./sidebar.component.css",

  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarComponent {
  navItems = input.required<INavItem[]>();
  userProfile = input.required<IUserProfile>();
}
