import { Component, signal, CUSTOM_ELEMENTS_SCHEMA, ChangeDetectionStrategy, inject } from "@angular/core";
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

  get options(): FormArray {
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
    if (this.pollForm.valid) {
      const formValue = this.pollForm.value;

      let expiryDateStr: string | null = null;

      if (formValue.expiryDate) {
        const date = new Date(formValue.expiryDate);

        expiryDateStr = date.toISOString();
      }

      const pollRequest: ICreatePollRequest = {
        question: formValue.question,
        description:
          formValue.description && formValue.description.trim() !== ""
            ? formValue.description
            : "No description provided",
        options: formValue.options.filter((opt: string) => opt && opt.trim() !== ""),
        multipleChoice: formValue.pollType === "multiple",
        expiresAt: expiryDateStr,
      };

      console.log("Sending payload to backend:", JSON.stringify(pollRequest, null, 2));

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
}
