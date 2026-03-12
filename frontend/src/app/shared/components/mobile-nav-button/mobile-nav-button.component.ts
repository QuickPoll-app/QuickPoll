import { Component, input, CUSTOM_ELEMENTS_SCHEMA, ChangeDetectionStrategy } from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-mobile-nav-button",
  imports: [CommonModule],
  templateUrl: "./mobile-nav-button.component.html",
  styleUrl: "./mobile-nav-button.component.css",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MobileNavButtonComponent {
  public icon = input.required<string>();
  public label = input.required<string>();
  public isActive = input<boolean>(false);
}
