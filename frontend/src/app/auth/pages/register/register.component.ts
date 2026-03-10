import { Component, ChangeDetectionStrategy, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { InputComponent } from "../../../shared/components/input/input.component";
import { ButtonsComponent } from "../../../shared/components/buttons/buttons.component";
import { AuthService } from "../../../services/auth.service";

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  
  return password && confirmPassword && password !== confirmPassword ? { passwordMismatch: true } : null;
}

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
  public errorMessage = signal<string | null>(null);

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registrationForm = this.fb.group({
      fullName: ["", [Validators.required]],
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(8)]],
      confirmPassword: ["", [Validators.required]],
    }, { validators: passwordMatchValidator });

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
    if (this.registrationForm.valid && !this.registrationForm.hasError('passwordMismatch')) {
      this.isLoading.set(true);
      this.errorMessage.set(null);
      
      const { fullName, email, password } = this.registrationForm.value;
      
      this.authService.register(fullName, email, password).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.router.navigate(['/']);
        },
        error: (error) => {
          this.isLoading.set(false);
          this.errorMessage.set(error.error?.message || 'Registration failed. Please try again.');
        }
      });
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
