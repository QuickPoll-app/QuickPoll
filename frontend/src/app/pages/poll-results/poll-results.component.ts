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

interface PollOption {
  id: string;
  label: string;
  votes: number;
}

interface Poll {
  id: string;
  title: string;
  description: string;
  type: string;
  expiresIn: string;
  totalVotes: number;
  participation: number;
  userVote: string;
  options: PollOption[];
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

  public poll = signal<Poll>({
    id: "1",
    title: "Programming language for next project",
    description:
      "Help us decide which programming language to use for our upcoming web application. Consider factors like team expertise, project requirements, and long-term maintainability.",
    type: "Single Choice",
    expiresIn: "2d 5h 23m",
    totalVotes: 142,
    participation: 73,
    userVote: "JavaScript (Node.js)",
    options: [
      { id: "1", label: "JavaScript (Node.js)", votes: 92 },
      { id: "2", label: "Python (Django/Flask)", votes: 28 },
      { id: "3", label: "Java (Spring Boot)", votes: 14 },
      { id: "4", label: "C# (.NET Core)", votes: 8 },
      { id: "5", label: "Go (Golang)", votes: 0 },
    ],
  });

  public totalVotes = computed(() => this.poll().totalVotes);
  public winnerOption = computed(() => {
    const options = this.poll().options;

    return options.reduce((max, opt) => (opt.votes > max.votes ? opt : max));
  });

  public optionsWithPercentages = computed(() => {
    const total = this.totalVotes();

    return this.poll().options.map((opt, idx) => ({
      ...opt,
      percentage: total > 0 ? Math.round((opt.votes / total) * 100) : 0,
      isWinner: opt.votes === this.winnerOption().votes && opt.votes > 0,
      isZeroVotes: opt.votes === 0,
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
