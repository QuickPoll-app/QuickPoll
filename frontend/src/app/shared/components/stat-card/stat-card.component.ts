import { Component, input, ChangeDetectionStrategy } from "@angular/core";

@Component({
  selector: "app-stat-card",
  imports: [],
  templateUrl: "./stat-card.component.html",
  styleUrl: "./stat-card.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatCardComponent {
  public value = input.required<string | number>();
  public label = input.required<string>();
  public trend = input<"positive" | "negative" | null>(null);
  public trendValue = input<string>("");
  public icon = input<string>("");
}
