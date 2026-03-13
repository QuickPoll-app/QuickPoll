import {
  Component,
  ChangeDetectionStrategy,
  OnInit,
  signal,
  DestroyRef,
  inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { IStatCard, IActivePoll, IRecentResult } from "../../models";
import { IPollResponse } from "../../models/poll.model";
import { DashboardService } from "../../services/dashboard.service";
import { AuthService } from "../../services/auth.service";
import { VoteTrackingService } from "../../services/vote-tracking.service";
import {
  StatCardComponent,
  BadgeComponent,
  ButtonsComponent,
  CardComponent,
  ProgressBarComponent,
  SkeletonStatCardComponent,
  SkeletonPollItemComponent,
  TrendingPollsComponent,
} from "../../shared/components";

@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrl: "./dashboard.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    StatCardComponent,
    BadgeComponent,
    CardComponent,
    ProgressBarComponent,
    ButtonsComponent,
    SkeletonStatCardComponent,
    SkeletonPollItemComponent,
    TrendingPollsComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class DashboardComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private router = inject(Router);
  private dashboardService = inject(DashboardService);
  private authService = inject(AuthService);
  private voteTrackingService = inject(VoteTrackingService);

  public stats = signal<IStatCard[]>([]);
  public activePolls = signal<IActivePoll[]>([]);
  public recentResults = signal<IRecentResult[]>([]);
  public trendingPolls = signal<IPollResponse[]>([]);
  public activePollsTotal = signal<number>(0);
  public statsLoading = signal(true);
  public activePollsLoading = signal(true);
  public recentResultsLoading = signal(true);
  public trendingLoading = signal(true);
  public error = signal<string | null>(null);
  public isAdmin = signal(false);

  constructor() {
    const user = this.authService.getUser();
    
    this.isAdmin.set(user?.role?.toLowerCase() === 'admin');
  }

  public ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.statsLoading.set(true);
    this.activePollsLoading.set(true);
    this.recentResultsLoading.set(true);
    this.trendingLoading.set(true);
    this.error.set(null);

    this.dashboardService
      .getStats()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (stats) => {
          this.stats.set(stats);
          this.statsLoading.set(false);
        },
        error: (error) => {
          console.error("Stats loading error:", error);
          this.statsLoading.set(false);
          this.stats.set([]);
        },
      });

    this.dashboardService
      .getActivePolls(0, 10)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.activePolls.set(response.polls);
          this.activePollsTotal.set(response.total);
          this.activePollsLoading.set(false);
        },
        error: (error) => {
          console.error("Active polls loading error:", error);
          this.activePollsLoading.set(false);
          this.error.set("Failed to load active polls");
        },
      });

    this.dashboardService
      .getRecentResults()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (results) => {
          this.recentResults.set(results);
          this.recentResultsLoading.set(false);
        },
        error: (error) => {
          console.error("Recent results loading error:", error);
          this.recentResultsLoading.set(false);
          this.recentResults.set([]);
        },
      });

    this.dashboardService
      .getTrendingPolls()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          const polls = response.data?.content || [];
          const updatedPolls = polls.map(p => ({
            ...p,
            HasVoted: p.HasVoted || this.voteTrackingService.hasVoted(p.id)
          }));
          
          this.trendingPolls.set(updatedPolls);

          this.trendingLoading.set(false);
        },
        error: (error) => {
          console.error("Trending polls loading error:", error);
          this.trendingLoading.set(false);
          this.trendingPolls.set([]);
        },
      });
  }

  public onTrendingPollClick(poll: IPollResponse) {
    if (poll.status === "CLOSED") {
      this.router.navigate(["/poll", poll.id, "results"]);
    } else if (poll.HasVoted) {
      this.router.navigate(["/poll", poll.id, "results"]);
    } else {
      const route = poll.multipleChoice ? "vote-multi" : "vote-single";

      this.router.navigate(["/poll", poll.id, route]);
    }
  }

  public onCreatePoll() {
    this.router.navigate(["/create-poll"]);
  }
}
