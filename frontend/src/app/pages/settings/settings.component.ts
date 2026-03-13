import { Component, inject, signal, OnInit, CUSTOM_ELEMENTS_SCHEMA, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';
import { ButtonsComponent } from '../../shared/components/buttons/buttons.component';
import { InputComponent } from '../../shared/components/input/input.component';
import { CardComponent } from '../../shared/components/card/card.component';
import { PasswordStrengthComponent } from './components/password-strength/password-strength.component';
import { ThemeSelectorComponent } from './components/theme-selector/theme-selector.component';
import { EmailChangeComponent } from './components/email-change/email-change.component';

@Component({
  selector: 'app-settings',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonsComponent,
    InputComponent,
    CardComponent,
    PasswordStrengthComponent,
    ThemeSelectorComponent,
    EmailChangeComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SettingsComponent implements OnInit {
  private fb = inject(FormBuilder);
  private themeService = inject(ThemeService);
  private authService = inject(AuthService);

  public passwordForm!: FormGroup;
  public isDarkMode = this.themeService.isDarkMode;
  public currentUser = signal(this.authService.getUser());
  public showPasswordToggle = signal(false);
  public showNewPasswordToggle = signal(false);
  public showConfirmPasswordToggle = signal(false);
  public passwordStrength = signal(0);
  public isLoadingPassword = signal(false);
  public isLoadingEmail = signal(false);

  ngOnInit() {
    this.initPasswordForm();
  }

  private initPasswordForm() {
    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
    });
  }

  public togglePasswordVisibility(field: 'current' | 'new' | 'confirm') {
    if (field === 'current') {
      this.showPasswordToggle.update(v => !v);
    } else if (field === 'new') {
      this.showNewPasswordToggle.update(v => !v);
    } else {
      this.showConfirmPasswordToggle.update(v => !v);
    }
  }

  public onNewPasswordChange(value: string) {
    this.calculatePasswordStrength(value);
  }

  private calculatePasswordStrength(password: string): void {
    let strength = 0;

    if (password.length >= 8) strength += 25;
    if (password.length >= 12) strength += 25;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength += 25;
    if (/\d/.test(password)) strength += 12.5;
    if (/[!@#$%^&*]/.test(password)) strength += 12.5;
    this.passwordStrength.set(Math.min(strength, 100));
  }

  public savePassword() {
    if (this.passwordForm.invalid) return;
    
    const { newPassword, confirmPassword } = this.passwordForm.value;

    if (newPassword !== confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.isLoadingPassword.set(true);
    // TODO: Call password update API
    setTimeout(() => {
      this.isLoadingPassword.set(false);
      alert('Password updated successfully');
      this.passwordForm.reset();
    }, 1000);
  }

  public onThemeChange(theme: 'light' | 'dark' | 'system') {
    if (theme === 'system') {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

      this.themeService.isDarkMode.set(prefersDark);
    } else {
      this.themeService.isDarkMode.set(theme === 'dark');
    }
  }

  public onEmailChangeStart() {
    this.isLoadingEmail.set(true);
  }

  public onEmailChangeComplete() {
    this.isLoadingEmail.set(false);
  }

 public  logout() {
    this.authService.logout();
  }
}
