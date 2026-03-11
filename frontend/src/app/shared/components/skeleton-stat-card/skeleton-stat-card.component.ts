import { Component, ChangeDetectionStrategy } from '@angular/core';
import { SkeletonComponent } from '../skeleton/skeleton.component';

@Component({
  selector: 'app-skeleton-stat-card',
  standalone: true,
  imports: [SkeletonComponent],
  template: `
    <div class="skeleton-stat-card">
      <app-skeleton variant="text" width="3rem" height="2rem"></app-skeleton>
      <app-skeleton variant="text" width="8rem" height="0.875rem"></app-skeleton>
      <div class="skeleton-trend">
        <app-skeleton variant="text" width="4rem" height="0.75rem"></app-skeleton>
      </div>
    </div>
  `,
  styleUrl: './skeleton-stat-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SkeletonStatCardComponent {}