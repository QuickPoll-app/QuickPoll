import { Component, output, input } from "@angular/core";

@Component({
  selector: "app-toggle",
  imports: [],
  templateUrl: "./toggle.component.html",
  styleUrl: "./toggle.component.css",
})
export class ToggleComponent {
  public checked = input(false);
  public disabled = input(false);
  public label = input<string>("");

  public toggleChange = output<boolean>();

  public handleToggle() {
    if (!this.disabled()) {
      this.toggleChange.emit(!this.checked());
    }
  }
}
