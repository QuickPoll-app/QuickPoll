import {
  Component,
  ChangeDetectionStrategy,
  signal,
  CUSTOM_ELEMENTS_SCHEMA,
  inject,
  OnInit,
  DestroyRef,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import {
  BadgeComponent,
  SidebarComponent,
  ButtonsComponent,
  ModalComponent,
} from "../../shared/components";
import { INavItem, IUserProfile } from "../../models/navigation.model";
import { IPollResponse } from "../../models";
import { AuthService } from "../../services/auth.service";
import { DashboardService } from "../../services/dashboard.service";
import { VoteTrackingService } from "../../services/vote-tracking.service";

@Component({
  selector: "app-poll-vote-single",
  templateUrl: "./poll-vote-single.component.html",
  styleUrl: "./poll-vote-single.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    FormsModule,
    ButtonsComponent,
    BadgeComponent,
    SidebarComponent,
    ModalComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PollVoteSingleComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private dashboardService = inject(DashboardService);
  private voteTrackingService = inject(VoteTrackingService);
  private destroyRef = inject(DestroyRef);

  public selectedOption = signal<string | null>(null);
  public isSubmitting = signal(false);
  public isSubmitted = signal(false);
  public showSuccessModal = signal(false);
  public error = signal<string | null>(null);
  public votedOptionLabel = signal<string>("");
  public poll = signal<IPollResponse | null>(null);
  public loading = signal(true);

  public navItems: INavItem[] = [
    { label: "Dashboard", icon: "lucide:layout-dashboard", route: "/dashboard" },
    { label: "Polls", icon: "lucide:vote", route: "/polls" },
    { label: "Create Poll", icon: "lucide:plus-circle", route: "/create-poll" },
  ];

  public userProfile: IUserProfile = (() => {
    const user = this.authService.getUser();

    return {
      name: user?.name || "Guest User",
      role: user?.role || "User",
    };
  })();

  public onOptionSelect(optionId: string) {
    this.selectedOption.set(optionId);
  }

  public onSubmitVote() {
    if (!this.selectedOption() || !this.poll()) return;

    const selectedOptionId = this.selectedOption()!;
    const option = this.poll()?.options.find((opt) => opt.id === selectedOptionId);

    this.isSubmitting.set(true);
    this.error.set(null);

    this.dashboardService
      .recordVote(this.poll()!.id, [selectedOptionId])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.isSubmitted.set(true);
          this.votedOptionLabel.set(option?.text || "");
          this.voteTrackingService.markAsVoted(this.poll()!.id);
          this.dashboardService
            .getPollById(this.poll()!.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
              next: (response) => {
                if (response.data) {
                  console.log('Updated poll after vote:', response.data.question, 'HasVoted:', response.data.HasVoted);
                  this.poll.set(response.data);
                }
                this.showSuccessModal.set(true);
              },
            });
        },
        error: (err) => {
          this.isSubmitting.set(false);
          this.error.set(err?.error?.message || "Failed to record vote");
        },
      });
  }

  public onCloseSuccessModal() {
    this.showSuccessModal.set(false);
    this.router.navigate(["/poll", this.poll()?.id, "results"]);
  }

  public onRetry() {
    this.error.set(null);
    this.onSubmitVote();
  }

  public onBackClick() {
    this.router.navigate(["/polls"]);
  }

  ngOnInit() {
    this.route.params.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const pollId = params["id"];

      if (pollId) {
        this.dashboardService
          .getAllPolls(0, 100)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (response) => {
              const foundPoll = response.data?.content?.find((p) => p.id === pollId);

              if (foundPoll) {
                this.poll.set(foundPoll);
              }
              this.loading.set(false);
            },
            error: () => {
              this.loading.set(false);
            },
          });
      }
    });
  }
}
