import { Component, input, output, ChangeDetectionStrategy } from "@angular/core";

@Component({
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: "app-buttons",
  imports: [],
  templateUrl: "./buttons.component.html",
  styleUrl: "./buttons.component.css",
})
export class ButtonsComponent {
  public variant = input<"primary" | "secondary" | "danger">("primary");
  public disabled = input(false);
  public loading = input(false);
  public type = input<"button" | "submit" | "reset">("button");

  public clicked = output<void>();
}
