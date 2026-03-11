import { Component, input, ChangeDetectionStrategy } from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-skeleton",
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
  styleUrl: "./skeleton.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SkeletonComponent {
  public variant = input<"text" | "card" | "circle" | "button" | "bar">("text");
  public width = input<string>("100%");
  public height = input<string>("1rem");
  public borderRadius = input<string>("0.25rem");
}
