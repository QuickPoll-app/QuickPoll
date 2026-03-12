import {
  Component,
  ChangeDetectionStrategy,
  signal,
  computed,
  effect,
  inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { ActivatedRoute, Router } from "@angular/router";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import {
  ResultsBarComponent,
  ResultsFooterComponent,
  SidebarComponent,
} from "../../shared/components";
import { INavItem, IUserProfile } from "../../models";
import { AuthService } from "../../services/auth.service";

interface OptionResponse {
  id: string;
  text: string;
  voteCount: number;
  percentage: number;
}

interface Poll {
  id: string;
  question: string;
  description: string;
  creatorName: string;
  HasVoted: boolean;
  status: string;
  multipleChoice: boolean;
  createdAt: string;
  expiresAt: string;
  totalVotes: number;
  participationRate: number;
  options: OptionResponse[];
}

@Component({
  selector: "app-poll-results",
  templateUrl: "./poll-results.component.html",
  styleUrl: "./poll-results.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, ResultsBarComponent, ResultsFooterComponent, SidebarComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PollResultsComponent {
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  public chartView = signal<"bar" | "pie">("bar");
  public lastUpdated = signal("2 min ago");

  public navItems = signal<INavItem[]>([
    { label: "Dashboard", icon: "lucide:layout-dashboard", route: "/dashboard" },
    { label: "Polls", icon: "lucide:vote", route: "/polls" },
    { label: "Create Poll", icon: "lucide:plus-circle", route: "/create-poll" },
  ]);

  public userProfile = signal<IUserProfile>(
    (() => {
      const user = this.authService.getUser();

      return {
        name: user?.name || "Guest User",
        role: user?.role || "User",
      };
    })(),
  );

  public poll = signal<Poll | null>(null);

  public totalVotes = computed(() => this.poll()?.totalVotes ?? 0);
  public winnerOption = computed(() => {
    const poll = this.poll();

    if (!poll) return null;
    return poll.options.reduce((max, opt) => (opt.voteCount > max.voteCount ? opt : max));
  });

  public optionsWithPercentages = computed(() => {
    const poll = this.poll();

    if (!poll) return [];
    return poll.options.map((opt, idx) => ({
      ...opt,
      isWinner: opt.voteCount === this.winnerOption()?.voteCount && opt.voteCount > 0,
      isZeroVotes: opt.voteCount === 0,
      animationDelay: idx * 100,
    }));
  });

  constructor() {
    effect(() => {
      const pollId = this.route.snapshot.paramMap.get("id");
      
      if (pollId) {
        // Load poll data based on ID
      }
    });
  }

  public setPollData(poll: Poll) {
    this.poll.set(poll);
  }

  public onChartViewChange(view: "bar" | "pie") {
    this.chartView.set(view);
  }

  public onRefresh() {
    this.lastUpdated.set("just now");
    // Trigger data refresh
  }

  public onBackClick() {
    this.router.navigate(["/polls"]);
  }
}
