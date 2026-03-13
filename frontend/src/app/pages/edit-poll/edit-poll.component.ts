import {
  Component,
  signal,
  CUSTOM_ELEMENTS_SCHEMA,
  ChangeDetectionStrategy,
  inject,
  OnInit,
  DestroyRef,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { PollService } from "../../services/poll.service";
import { DashboardService } from "../../services/dashboard.service";
import { ICreatePollRequest, IPollResponse } from "../../models/poll.model";
import { ButtonsComponent } from "../../shared/components/buttons/buttons.component";
import { ToggleComponent } from "../../shared/components/toggle/toggle.component";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: "app-edit-poll",
  imports: [CommonModule, ReactiveFormsModule, ButtonsComponent, ToggleComponent],
  templateUrl: "./edit-poll.component.html",
  styleUrl: "./edit-poll.component.css",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class EditPollComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pollService = inject(PollService);
  private dashboardService = inject(DashboardService);
  private destroyRef = inject(DestroyRef);

  public pollForm: FormGroup;
  public questionCharCount = signal(0);
  public descriptionCharCount = signal(0);
  public anonymousVoting = signal(false);
  public dateError = signal<string | null>(null);
  public loading = signal(true);
  public pollId = signal<string | null>(null);

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

    this.pollForm.get("expiryDate")?.valueChanges.subscribe((value) => {
      this.validateExpiryDate(value);
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

  private validateExpiryDate(dateValue: string): void {
    if (!dateValue) {
      this.dateError.set(null);
      return;
    }

    const selectedDate = new Date(dateValue);
    const today = new Date();

    today.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
      this.dateError.set("Please select a date in the future. Past dates are not allowed.");
    } else {
      this.dateError.set(null);
    }
  }

  private loadPoll(pollId: string): void {
    this.dashboardService
      .getPollById(pollId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          if (response.data) {
            this.populateForm(response.data);
            this.loading.set(false);
          }
        },
        error: (error) => {
          console.error("Error loading poll:", error);
          this.loading.set(false);
          this.router.navigate(["/polls"]);
        },
      });
  }

  private populateForm(poll: IPollResponse): void {
    const expiryDate = new Date(poll.expiresAt).toISOString().split("T")[0];

    this.pollForm.patchValue({
      question: poll.question,
      description: poll.description,
      pollType: poll.multipleChoice ? "multiple" : "single",
      expiryDate,
    });

    this.questionCharCount.set(poll.question.length);
    this.descriptionCharCount.set(poll.description.length);

    const optionsArray = this.pollForm.get("options") as FormArray;

    optionsArray.clear();

    poll.options.forEach((option) => {
      optionsArray.push(this.fb.control(option.text, Validators.required));
    });
  }

  public onSubmit() {
    if (!this.pollForm.valid || this.dateError()) {
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

    const pollId = this.pollId();

    if (!pollId) return;

    this.pollService.updatePoll(pollId, pollRequest)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.router.navigate(["/polls"]);
        },
        error: (error) => {
          console.error("Error updating poll", error);
        },
      });
  }

  ngOnInit() {
    const pollId = this.route.snapshot.paramMap.get("id");
    
    if (pollId) {
      this.pollId.set(pollId);
      this.loadPoll(pollId);
    } else {
      this.router.navigate(["/polls"]);
    }
  }
}
