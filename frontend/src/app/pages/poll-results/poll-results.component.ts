import {
  Component,
  ChangeDetectionStrategy,
  signal,
  computed,
  inject,
  OnInit,
  DestroyRef,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { ActivatedRoute, Router } from "@angular/router";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import {
  PieChartComponent,
  ResultsBarComponent,
  ResultsFooterComponent,
  SidebarComponent,
} from "../../shared/components";
import { INavItem, IUserProfile } from "../../models";
import { IPollResultsResponse } from "../../models/poll.model";
import { AuthService } from "../../services/auth.service";
import { DashboardService } from "../../services/dashboard.service";

@Component({
  selector: "app-poll-results",
  templateUrl: "./poll-results.component.html",
  styleUrl: "./poll-results.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, PieChartComponent, ResultsBarComponent, ResultsFooterComponent, SidebarComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PollResultsComponent implements OnInit {
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private dashboardService = inject(DashboardService);
  private destroyRef = inject(DestroyRef);

  public chartView = signal<"bar" | "pie">("bar");
  public lastUpdated = signal("just now");
  public loading = signal(true);

  public navItems = signal<INavItem[]>(
    (() => {
      const user = this.authService.getUser();
      const isAdmin = user?.role?.toLowerCase() === "admin";
      const items: INavItem[] = [
        { label: "Dashboard", icon: "lucide:layout-dashboard", route: "/dashboard" },
        { label: "Polls", icon: "lucide:vote", route: "/polls" },
      ];

      if (isAdmin) {
        items.push({ label: "Create Poll", icon: "lucide:plus-circle", route: "/create-poll" });
      }
      return items;
    })(),
  );

  public userProfile = signal<IUserProfile>(
    (() => {
      const user = this.authService.getUser();

      return {
        name: user?.name || "Guest User",
        role: user?.role || "User",
      };
    })(),
  );

  public poll = signal<IPollResultsResponse | null>(null);

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

  ngOnInit() {
    const pollId = this.route.snapshot.paramMap.get("id");

    if (pollId) {
      this.loading.set(true);
      this.dashboardService
        .getPollById(pollId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (response) => {
            if (response.data) {
              this.poll.set(response.data as IPollResultsResponse);
            }
            this.loading.set(false);
          },
          error: (error) => {
            console.error("Error fetching poll results:", error);
            this.loading.set(false);
          },
        });
    }
  }

  public onChartViewChange(view: "bar" | "pie") {
    this.chartView.set(view);
  }

  public onRefresh() {
    const pollId = this.route.snapshot.paramMap.get("id");

    if (pollId) {
      this.dashboardService
        .getPollById(pollId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (response) => {
            if (response.data) {
              this.poll.set(response.data as IPollResultsResponse);
              this.lastUpdated.set("just now");
            }
          },
          error: (error) => {
            console.error("Error refreshing poll results:", error);
          },
        });
    }
  }

  public onBackClick() {
    this.router.navigate(["/polls"]);
  }
}
