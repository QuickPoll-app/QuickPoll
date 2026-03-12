import { Component, ChangeDetectionStrategy, input } from "@angular/core";
import { CommonModule } from "@angular/common";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";

@Component({
  selector: "app-winner-callout",
  templateUrl: "./winner-callout.component.html",
  styleUrl: "./winner-callout.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class WinnerCalloutComponent {
  public winnerLabel = input.required<string>();
  public winnerPercentage = input.required<number>();
  public winnerVotes = input.required<number>();
}
