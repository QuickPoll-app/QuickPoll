import {
  Component,
  input,
  output,
  signal,
  OnInit,
  inject,
  CUSTOM_ELEMENTS_SCHEMA,
  ChangeDetectionStrategy,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";

@Component({
  selector: "app-email-change",
  imports: [CommonModule, ReactiveFormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: "./email-change.component.html",
  styleUrl: "./email-change.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailChangeComponent implements OnInit {
  private fb = inject(FormBuilder);

  public currentEmail = input("");
  public changeStart = output<void>();
  public changeComplete = output<void>();

  public showChangeForm = signal(false);
  public emailForm!: FormGroup;
  public isLoading = signal(false);

  ngOnInit() {
    this.emailForm = this.fb.group({
      newEmail: ["", [Validators.required, Validators.email]],
    });
  }

  public toggleChangeForm() {
    this.showChangeForm.update((v) => !v);
    if (!this.showChangeForm()) {
      this.emailForm.reset();
    }
  }

  public saveEmail() {
    if (this.emailForm.invalid) return;

    this.isLoading.set(true);
    this.changeStart.emit();

    // TODO: Call email update API
    setTimeout(() => {
      this.isLoading.set(false);
      this.changeComplete.emit();
      alert("Verification email sent to your new address");
      this.showChangeForm.set(false);
      this.emailForm.reset();
    }, 1000);
  }

  public cancel() {
    this.showChangeForm.set(false);
    this.emailForm.reset();
  }
}
