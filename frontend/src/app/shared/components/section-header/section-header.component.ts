import { ChangeDetectionStrategy, Component, input } from "@angular/core";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";

@Component({
  selector: "app-section-header",
  templateUrl: "./section-header.component.html",
  styleUrl: "./section-header.component.css",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionHeaderComponent {
  public number = input.required<number>();
  public title = input.required<string>();
}
