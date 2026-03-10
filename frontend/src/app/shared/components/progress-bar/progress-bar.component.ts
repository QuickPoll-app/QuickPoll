import { ChangeDetectionStrategy, Component, computed, input } from "@angular/core";

@Component({
  selector: "app-progress-bar",
  imports: [],
  templateUrl: "./progress-bar.component.html",
  styleUrl: "./progress-bar.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProgressBarComponent {
  public value = input.required<number>();
  public max = input(100);
  public showLabel = input(false);

  public percentage = computed(() => {
    const val = this.value();
    const maxVal = this.max();
    
    return maxVal <= 0 ? 0 : Math.min(100, Math.max(0, (val / maxVal) * 100));
  });
}
