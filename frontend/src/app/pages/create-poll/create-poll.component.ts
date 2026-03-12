import {
  Component,
  signal,
  CUSTOM_ELEMENTS_SCHEMA,
  ChangeDetectionStrategy,
  inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { PollService } from "../../services/poll.service";
import { ICreatePollRequest } from "../../models/poll.model";
import { ButtonsComponent } from "../../shared/components/buttons/buttons.component";
import { ToggleComponent } from "../../shared/components/toggle/toggle.component";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: "app-create-poll",
  imports: [CommonModule, ReactiveFormsModule, ButtonsComponent, ToggleComponent],
  templateUrl: "./create-poll.component.html",
  styleUrl: "./create-poll.component.css",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class CreatePollComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private pollService = inject(PollService);

  public pollForm: FormGroup;
  public questionCharCount = signal(0);
  public descriptionCharCount = signal(0);
  public anonymousVoting = signal(false);

  constructor() {
    this.pollForm = this.fb.group({
      question: ["", [Validators.required, Validators.maxLength(200)]],
      description: ["", Validators.maxLength(500)],
      pollType: ["single", Validators.required],
      expiryDate: ["", Validators.required],
      options: this.fb.array([
        this.fb.control("", Validators.required),
        this.fb.control("", Validators.required),
      ]),
    });

    this.pollForm.get("question")?.valueChanges.subscribe((value) => {
      this.questionCharCount.set(value?.length || 0);
    });

    this.pollForm.get("description")?.valueChanges.subscribe((value) => {
      this.descriptionCharCount.set(value?.length || 0);
    });
  }

  public get options(): FormArray {
    return this.pollForm.get("options") as FormArray;
  }

  public addOption() {
    this.options.push(this.fb.control("", Validators.required));
  }

  public removeOption(index: number) {
    if (this.options.length > 2) {
      this.options.removeAt(index);
    }
  }

  public onToggleAnonymous(checked: boolean) {
    this.anonymousVoting.set(checked);
  }

  public onCancel() {
    this.router.navigate(["/polls"]);
  }

  public onSubmit() {
    if (!this.pollForm.valid) {
      this.pollForm.markAllAsTouched();
      return;
    }

    const formValue = this.pollForm.getRawValue();

    const options = ((formValue.options ?? []) as string[])
      .map((opt: string) => (opt ?? "").toString().trim())
      .filter((opt: string) => opt.length > 0);

    const uniqueOptions = Array.from(new Set<string>(options));

    if (uniqueOptions.length < 2) {
      console.error("Poll must have at least 2 unique options");
      return;
    }

    const pollRequest: ICreatePollRequest = {
      question: (formValue.question ?? "").trim(),
      description:
        formValue.description && formValue.description.trim() !== ""
          ? formValue.description.trim()
          : "No description provided",
      options: uniqueOptions,
      multipleChoice: formValue.pollType === "multiple",
      expiresAt: formValue.expiryDate ? `${formValue.expiryDate}T23:59:59Z` : null,
    };

    this.pollService.createPoll(pollRequest).subscribe({
      next: () => {
        this.router.navigate(["/polls"]);
      },
      error: (error) => {
        console.error("Error creating poll", error);
      },
    });
  }
}
