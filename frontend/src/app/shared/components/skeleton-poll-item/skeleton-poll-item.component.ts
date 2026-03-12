import { Component, ChangeDetectionStrategy } from '@angular/core';
import { SkeletonComponent } from '../skeleton/skeleton.component';

@Component({
  selector: 'app-skeleton-poll-item',
  standalone: true,
  imports: [SkeletonComponent],
  template: `
    <div class="skeleton-poll-item">
      <div class="skeleton-header">
        <app-skeleton variant="text" width="12rem" height="1rem"></app-skeleton>
        <app-skeleton variant="text" width="4rem" height="1.25rem"></app-skeleton>
      </div>
      <div class="skeleton-body">
        <div class="skeleton-participation">
          <app-skeleton variant="text" width="8rem" height="0.75rem"></app-skeleton>
          <app-skeleton variant="text" width="2rem" height="0.75rem"></app-skeleton>
        </div>
        <app-skeleton variant="bar" width="100%" height="0.5rem"></app-skeleton>
      </div>
      <div class="skeleton-footer">
        <app-skeleton variant="text" width="6rem" height="1rem"></app-skeleton>
      </div>
    </div>
  `,
  styleUrl: './skeleton-poll-item.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SkeletonPollItemComponent {}