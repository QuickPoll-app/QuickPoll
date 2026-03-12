import { Component, input, ChangeDetectionStrategy } from "@angular/core";

@Component({
  selector: "app-badge",
  standalone: true,
  imports: [],
  templateUrl: "./badge.component.html",
  styleUrl: "./badge.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BadgeComponent {
  variant = input<"active" | "expired" | "type" | "anonymous" | "voted" | "admin" | "user">(
    "active",
  );
}
