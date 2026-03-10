import { ChangeDetectionStrategy, Component } from "@angular/core";

@Component({
  selector: "app-spinner",
  imports: [],
  templateUrl: "./spinner.component.html",
  styleUrl: "./spinner.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpinnerComponent {}
