import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-skeleton',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div 
      class="skeleton" 
      [class]="'skeleton-' + variant()"
      [style.width]="width()"
      [style.height]="height()"
      [style.border-radius]="borderRadius()"
    ></div>
  `,
  styleUrl: './skeleton.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SkeletonComponent {
  variant = Input<'text' | 'card' | 'circle' | 'button' | 'bar'>('text');
  width = Input<string>('100%');
  height = Input<string>('1rem');
  borderRadius = Input<string>('0.25rem');
}