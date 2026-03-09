import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

@Component({
  selector: 'app-toast',
  imports: [],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ToastComponent {
 public message = input.required<string>();
 public variant = input<'success' | 'error' | 'warning' | 'info'>('info');
 public autoClose = input(true);
 public duration = input(4000);
 
 public close = output<void>();
}
