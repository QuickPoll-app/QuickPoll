import { Component, ChangeDetectionStrategy } from '@angular/core';
import { SkeletonComponent } from '../skeleton/skeleton.component';

@Component({
  selector: 'app-skeleton-poll-card',
  standalone: true,
  imports: [SkeletonComponent],
  template: `
    <article class="skeleton-poll-card">
      <app-skeleton variant="text" width="80%" height="1.5rem"></app-skeleton>
      <div class="skeleton-meta">
        <app-skeleton variant="text" width="60%" height="0.875rem"></app-skeleton>
      </div>
      <div class="skeleton-options">
        @for (i of [1,2,3]; track i) {
          <div class="skeleton-option">
            <div class="skeleton-option-header">
              <app-skeleton variant="text" width="40%" height="1rem"></app-skeleton>
              <app-skeleton variant="text" width="3rem" height="1rem"></app-skeleton>
            </div>
            <app-skeleton variant="bar" width="100%" height="0.5rem"></app-skeleton>
          </div>
        }
      </div>
    </article>
  `,
  styleUrl: './skeleton-poll-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SkeletonPollCardComponent {}