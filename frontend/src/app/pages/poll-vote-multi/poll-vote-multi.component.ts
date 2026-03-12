import {
  Component,
  ChangeDetectionStrategy,
  signal,
  CUSTOM_ELEMENTS_SCHEMA,
  inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import {
  BadgeComponent,
  SidebarComponent,
  ButtonsComponent,
  ModalComponent,
} from "../../shared/components";
import { INavItem, IUserProfile } from "../../shared/components/sidebar/sidebar.component";
import { IVotePollOption } from "../../models";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: "app-poll-vote-multi",
  templateUrl: "./poll-vote-multi.component.html",
  styleUrl: "./poll-vote-multi.component.css",
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
export class PollVoteMultiComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  public selectedOptions = signal<Set<string>>(new Set());
  public isSubmitting = signal(false);
  public isSubmitted = signal(false);
  public showSuccessModal = signal(false);
  public error = signal<string | null>(null);
  public votedOptionsLabels = signal<string[]>([]);

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

  public poll = {
    id: "2",
    title: "Team building activities",
    description:
      "Select all team building activities you would be interested in participating in. We want to plan activities that appeal to as many team members as possible.",
    type: "Multiple Choice",
    timeLeft: "5d 12h",
    votes: 89,
  };

  public options: IVotePollOption[] = [
    {
      id: "escape-room",
      label: "Escape Room Challenge",
      description: "Work together to solve puzzles and escape themed rooms within a time limit.",
    },
    {
      id: "cooking-class",
      label: "Cooking Class",
      description: "Learn to prepare a meal together with a professional chef instructor.",
    },
    {
      id: "outdoor-adventure",
      label: "Outdoor Adventure Course",
      description: "Navigate obstacle courses and zip lines in a natural outdoor setting.",
    },
    {
      id: "bowling",
      label: "Bowling Tournament",
      description: "Friendly competition with teams and prizes in a relaxed bowling alley setting.",
    },
    {
      id: "trivia-night",
      label: "Trivia Night",
      description: "Test your knowledge in various categories while enjoying refreshments.",
    },
    {
      id: "art-workshop",
      label: "Art Workshop",
      description: "Create art pieces together with guided instruction from a local artist.",
    },
  ];

  public onOptionToggle(optionId: string) {
    const updated = new Set(this.selectedOptions());

    if (updated.has(optionId)) {
      updated.delete(optionId);
    } else {
      updated.add(optionId);
    }
    this.selectedOptions.set(updated);
  }

  public isOptionSelected(optionId: string): boolean {
    return this.selectedOptions().has(optionId);
  }

  public onSubmitVote() {
    if (this.selectedOptions().size < 2) return;

    const selectedIds = Array.from(this.selectedOptions());
    const selectedLabels = this.options
      .filter((opt) => selectedIds.includes(opt.id))
      .map((opt) => opt.label);

    this.isSubmitting.set(true);
    this.error.set(null);

    // TODO: Replace with actual API call
    setTimeout(() => {
      // Simulate success
      this.isSubmitting.set(false);
      this.isSubmitted.set(true);
      this.votedOptionsLabels.set(selectedLabels);
      this.showSuccessModal.set(true);
    }, 1500);
  }

  public onCloseSuccessModal() {
    this.showSuccessModal.set(false);
    this.router.navigate(["/poll", this.poll.id, "results"]);
  }

  public onRetry() {
    this.error.set(null);
    this.onSubmitVote();
  }

  public onBackClick() {
    this.router.navigate(["/polls"]);
  }
}
