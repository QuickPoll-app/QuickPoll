import { Component, ChangeDetectionStrategy, input, computed } from "@angular/core";
import { CommonModule } from "@angular/common";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";

@Component({
  selector: "app-results-bar",
  templateUrl: "./results-bar.component.html",
  styleUrl: "./results-bar.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ResultsBarComponent {
  public label = input.required<string>();
  public percentage = input.required<number>();
  public votes = input.required<number>();
  public isWinner = input(false);
  public isZeroVotes = input(false);
  public animationDelay = input(0);

  public barWidth = computed(() => `${this.percentage()}%`);

  public barStyle = computed(() => ({
    "animation-delay": `${this.animationDelay()}ms`,
  }));
}
