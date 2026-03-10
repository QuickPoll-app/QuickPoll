import { Component, output, input, ChangeDetectionStrategy } from "@angular/core";

@Component({
  selector: "app-option-row",
  imports: [],
  templateUrl: "./option-row.component.html",
  styleUrl: "./option-row.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OptionRowComponent {
  public label = input.required<string>();
  selected = input(false);
  disabled = input(false);
  type = input<"radio" | "checkbox">("radio");

  selectionChange = output<void>();
}
