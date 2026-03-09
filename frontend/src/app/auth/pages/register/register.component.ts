import { Component, ChangeDetectionStrategy, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { InputComponent } from "../../../shared/components/input/input.component";
import { ButtonsComponent } from "../../../shared/components/buttons/buttons.component";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: "app-register",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, InputComponent, ButtonsComponent],
  templateUrl: "./register.component.html",
  styleUrl: "./register.component.css",
})
export class RegisterComponent {
  public registrationForm: FormGroup;
  public showPassword = signal(false);
  public showConfirmPassword = signal(false);
  public isLoading = signal(false);
  public passwordStrength = signal<"weak" | "medium" | "strong">("weak");
  public passwordsMatch = signal(false);

  constructor(private fb: FormBuilder) {
    this.registrationForm = this.fb.group({
      fullName: ["", [Validators.required]],
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(8)]],
      confirmPassword: ["", [Validators.required]],
    });

    this.registrationForm.get("password")?.valueChanges.subscribe((value) => {
      this.calculatePasswordStrength(value);
      this.checkPasswordsMatch();
    });

    this.registrationForm.get("confirmPassword")?.valueChanges.subscribe(() => {
      this.checkPasswordsMatch();
    });
  }

  calculatePasswordStrength(password: string) {
    if (!password) {
      this.passwordStrength.set("weak");
      return;
    }

    let strength = 0;

    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[^a-zA-Z0-9]/.test(password)) strength++;

    if (strength <= 2) this.passwordStrength.set("weak");
    else if (strength === 3) this.passwordStrength.set("medium");
    else this.passwordStrength.set("strong");
  }

  public checkPasswordsMatch() {
    const password = this.registrationForm.get("password")?.value;
    const confirmPassword = this.registrationForm.get("confirmPassword")?.value;

    this.passwordsMatch.set(password && confirmPassword && password === confirmPassword);
  }

  public togglePasswordVisibility() {
    this.showPassword.update((v) => !v);
  }

  public toggleConfirmPasswordVisibility() {
    this.showConfirmPassword.update((v) => !v);
  }

  public onSubmit() {
    if (this.registrationForm.valid) {
      this.isLoading.set(true);
      console.log("Registration data:", this.registrationForm.value);
      setTimeout(() => this.isLoading.set(false), 2000);
    }
  }

  get fullName() {
    return this.registrationForm.get("fullName");
  }

  get email() {
    return this.registrationForm.get("email");
  }

  get password() {
    return this.registrationForm.get("password");
  }

  get confirmPassword() {
    return this.registrationForm.get("confirmPassword");
  }
}
