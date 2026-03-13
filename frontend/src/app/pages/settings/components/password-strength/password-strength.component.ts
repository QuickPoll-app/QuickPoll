import {
  Component,
  input,
  computed,
  CUSTOM_ELEMENTS_SCHEMA,
  ChangeDetectionStrategy,
} from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-password-strength",
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: "./password-strength.component.html",
  styleUrl: "./password-strength.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PasswordStrengthComponent {
  public strength = input(0);

  public strengthLabel = computed(() => {
    const s = this.strength();

    if (s < 25) return "Weak";
    if (s < 50) return "Fair";
    if (s < 75) return "Good";
    return "Strong";
  });

  public strengthColor = computed(() => {
    const s = this.strength();

    if (s < 25) return "#c53030";
    if (s < 50) return "#d97706";
    if (s < 75) return "#f59e0b";
    return "#10b981";
  });
}
