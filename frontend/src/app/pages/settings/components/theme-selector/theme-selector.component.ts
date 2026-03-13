import {
  Component,
  input,
  output,
  CUSTOM_ELEMENTS_SCHEMA,
  ChangeDetectionStrategy,
} from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-theme-selector",
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: "./theme-selector.component.html",
  styleUrl: "./theme-selector.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThemeSelectorComponent {
  isDarkMode = input(false);
  themeChange = output<"light" | "dark" | "system">();

  public selectTheme(theme: "light" | "dark" | "system") {
    this.themeChange.emit(theme);
  }
}
