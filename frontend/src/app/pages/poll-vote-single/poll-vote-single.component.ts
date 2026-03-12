import { Component, ChangeDetectionStrategy, signal, CUSTOM_ELEMENTS_SCHEMA, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { BadgeComponent, SidebarComponent, ButtonsComponent, ModalComponent } from "../../shared/components";
import { INavItem, IUserProfile } from "../../models/navigation.model";
import { IVotePollOption } from "../../models";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: "app-poll-vote-single",
  templateUrl: "./poll-vote-single.component.html",
  styleUrl: "./poll-vote-single.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, ButtonsComponent, BadgeComponent, SidebarComponent, ModalComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PollVoteSingleComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  public selectedOption = signal<string | null>(null);
  public isSubmitting = signal(false);
  public isSubmitted = signal(false);
  public showSuccessModal = signal(false);
  public error = signal<string | null>(null);
  public votedOptionLabel = signal<string>('');

  public navItems: INavItem[] = [
    { label: "Dashboard", icon: "lucide:layout-dashboard", route: "/dashboard" },
    { label: "Polls", icon: "lucide:vote", route: "/polls" },
    { label: "Create Poll", icon: "lucide:plus-circle", route: "/create-poll" },
  ];

  public userProfile: IUserProfile = (() => {
    const user = this.authService.getUser();
    
    return {
      name: user?.name || 'Guest User',
      role: user?.role || 'User'
    };
  })();

  public poll = {
    id: "1",
    title: "Programming language for next project",
    description:
      "Help us decide which programming language to use for our upcoming web application. Consider factors like team expertise, project requirements, and long-term maintainability.",
    type: "Single Choice",
    isAnonymous: true,
    timeLeft: "2d 5h 23m",
    votes: 142,
    expiresIn: "2d 5h 23m",
  };

  public options: IVotePollOption[] = [
    {
      id: "javascript",
      label: "JavaScript (Node.js)",
      description: "Popular, large ecosystem, team familiarity",
    },
    {
      id: "python",
      label: "Python (Django/Flask)",
      description: "Rapid development, clean syntax, strong frameworks",
    },
    {
      id: "java",
      label: "Java (Spring Boot)",
      description: "Enterprise-grade, robust, excellent performance",
    },
    {
      id: "csharp",
      label: "C# (.NET Core)",
      description: "Microsoft ecosystem, strong tooling, cross-platform",
    },
    {
      id: "go",
      label: "Go (Golang)",
      description: "High performance, simple syntax, excellent concurrency",
    },
  ];

  public onOptionSelect(optionId: string) {
    this.selectedOption.set(optionId);
  }

  public onSubmitVote() {
    if (!this.selectedOption()) return;

    const selectedOptionId = this.selectedOption();
    const option = this.options.find(opt => opt.id === selectedOptionId);
    
    this.isSubmitting.set(true);
    this.error.set(null);

    // TODO: Replace with actual API call
    setTimeout(() => {
      // Simulate success
      this.isSubmitting.set(false);
      this.isSubmitted.set(true);
      this.votedOptionLabel.set(option?.label || '');
      this.showSuccessModal.set(true);
    }, 1500);
  }

  public onCloseSuccessModal() {
    this.showSuccessModal.set(false);
    this.router.navigate(['/poll', this.poll.id, 'results']);
  }

  public onRetry() {
    this.error.set(null);
    this.onSubmitVote();
  }

  public onBackClick() {
    this.router.navigate(["/polls"]);
  }
}
