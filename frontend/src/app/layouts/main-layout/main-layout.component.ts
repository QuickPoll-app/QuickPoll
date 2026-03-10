import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { SidebarComponent } from "../../shared/components/sidebar/sidebar.component";
import { INavItem, IUserProfile } from "../../models";

@Component({
  selector: "app-main-layout",
  imports: [RouterOutlet, SidebarComponent],
  templateUrl: "./main-layout.component.html",
  styleUrl: "./main-layout.component.css",
})
export class MainLayoutComponent {
  public navItems: INavItem[] = [
    {
      label: "Dashboard",
      icon: "lucide:layout-dashboard",
      route: "/dashboard",
    },
    { label: "Polls", icon: "lucide:vote", route: "/polls" },
    { label: "Create Poll", icon: "lucide:plus-circle", route: "/create-poll" },
  ];

  public userProfile: IUserProfile = {
    name: "John Smith",
    role: "Admin",
    avatar:
      "https://static.paraflowcontent.com/public/resource/image/7cf57088-a37e-4475-a598-f986087fdec1.jpeg",
  };
}
