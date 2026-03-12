import {
  Component,
  ChangeDetectionStrategy,
  input,
  output,
  signal,
  effect,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

@Component({
  selector: 'app-results-footer',
  templateUrl: './results-footer.component.html',
  styleUrl: './results-footer.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ResultsFooterComponent {
  public totalVotes = input.required<number>();
  public participation = input.required<number>();
  public lastUpdated = input.required<string>();
  public isExpired = input(false);
  public closedDate = input<string>('');

  public refreshClick = output<void>();

  public autoRefreshCountdown = signal(15);
  public showAutoRefresh = signal(!this.isExpired());

  constructor() {
    effect(() => {
      if (!this.isExpired() && this.showAutoRefresh()) {
        const interval = setInterval(() => {
          this.autoRefreshCountdown.update((v) => (v > 0 ? v - 1 : 15));
        }, 1000);

        return () => clearInterval(interval);
      }
      return undefined;
    });
  }

  public getParticipationColor(): string {
    const p = this.participation();
    
    if (p < 50) return 'rgba(197, 48, 48, 1)';
    if (p < 75) return 'rgba(217, 119, 6, 1)';
    return 'rgba(45, 122, 79, 1)';
  }

  public onRefresh() {
    this.autoRefreshCountdown.set(15);
    this.refreshClick.emit();
  }
}
