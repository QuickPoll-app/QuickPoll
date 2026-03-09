/* eslint-disable @typescript-eslint/explicit-function-return-type */
import { ChangeDetectionStrategy, Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { ButtonsComponent } from "../../../shared/components/buttons/buttons.component";
import { InputComponent } from "../../../shared/components/input/input.component";
// import { AuthService } from "../../../services/auth.service";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: "app-login",
  imports: [CommonModule, ReactiveFormsModule, RouterLink, InputComponent, ButtonsComponent],
  templateUrl: "./login.component.html",
  styleUrl: "./login.component.css",
})
export class LoginComponent {
  public loginForm: FormGroup;
  public isLoading = signal(false);
  public showError = signal(false);
  public showPassword = signal(false);

  constructor(
    private fb: FormBuilder,
    private router: Router,
  ) {
    this.loginForm = this.fb.group({
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required]],
    });
  }

  public togglePasswordVisibility() {
    this.showPassword.update((v) => !v);
  }

  public onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading.set(true);
      this.showError.set(false);
      console.log("Login data:", this.loginForm.value);

      setTimeout(() => {
        this.isLoading.set(false);
        this.router.navigate(["/dashboard"]);
      }, 2000);
    }
  }

  get email() {
    return this.loginForm.get("email");
  }

  get password() {
    return this.loginForm.get("password");
  }
}
