import { ChangeDetectionStrategy, Component, input, output } from "@angular/core";
import { BadgeComponent } from "../badge/badge.component";

@Component({
  selector: "app-poll-card",
  imports: [BadgeComponent],
  templateUrl: "./poll-card.component.html",
  styleUrl: "./poll-card.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PollCardComponent {
  public title = input.required<string>();
  public description = input<string>("");
  public status = input<"active" | "expired">("active");
  public pollType = input<"single" | "multi">("single");
  public isVoted = input(false);
  public isAnonymous = input(false);
  public votes = input<number>(0);
  public participation = input<number>(0);
  public timeLeft = input<string>("");

  public cardClick = output<void>();
}
