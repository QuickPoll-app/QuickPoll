import { ChangeDetectionStrategy, Component, input, output, effect } from "@angular/core";
import { CommonModule } from "@angular/common";
import { BadgeComponent } from "../badge/badge.component";
import { IPollResponse } from "../../../models";

@Component({
  selector: "app-poll-card",
  standalone: true,
  imports: [BadgeComponent, CommonModule],
  templateUrl: "./poll-card.component.html",
  styleUrl: "./poll-card.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PollCardComponent {
  public poll = input.required<IPollResponse>();
  public cardClick = output<void>();

  constructor() {
    effect(() => {
      console.log('Poll card received:', this.poll().question, 'HasVoted:', this.poll().HasVoted);
    });
  }

  public getStatusVariant() {
    return this.poll().status === 'ACTIVE' ? 'active' : 'expired';
  }

  public getPollType() {
    return this.poll().multipleChoice ? 'multi' : 'single';
  }

  public getTimeLeftStatus() {
    const now = new Date().getTime();
    const expiry = new Date(this.poll().expiresAt).getTime();
    const hoursLeft = (expiry - now) / (1000 * 60 * 60);

    return hoursLeft < 24 ? 'warning' : 'ok';
  }

  public calculateTimeLeft(): string {
    const now = new Date().getTime();
    const expiry = new Date(this.poll().expiresAt).getTime();
    const diff = expiry - now;

    if (diff <= 0) {
      return 'Expired';
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));

    if (days > 0) {
      return `${days} day${days > 1 ? 's' : ''} left`;
    } else if (hours > 0) {
      return `${hours} hour${hours > 1 ? 's' : ''} left`;
    } else {
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

      return `${minutes} minute${minutes > 1 ? 's' : ''} left`;
    }
  }

  public getTopOptions() {
    return this.poll().options.slice(0, 2).sort((a, b) => b.percentage - a.percentage);
  }
}
