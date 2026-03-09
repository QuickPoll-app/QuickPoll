import { Component, output, input } from '@angular/core';

@Component({
  selector: 'app-toggle',
  imports: [],
  templateUrl: './toggle.component.html',
  styleUrl: './toggle.component.css'
})
export class ToggleComponent {
  public checked = input(false);
  public disabled = input(false);
  public label = input<string>('');
  
  public change = output<boolean>();

  public handleToggle() {
    if (!this.disabled()) {
      this.change.emit(!this.checked());
    }
  }
}
