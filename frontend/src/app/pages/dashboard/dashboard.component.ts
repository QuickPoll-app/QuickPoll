import { Component, ChangeDetectionStrategy, OnInit, signal, DestroyRef, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { IStatCard, IActivePoll, IRecentResult } from "../../models";
import { DashboardService } from "../../services/dashboard.service";
import {
  StatCardComponent,
  BadgeComponent,
  ButtonsComponent,
  CardComponent,
  ProgressBarComponent,
  SkeletonStatCardComponent,
  SkeletonPollItemComponent,
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
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class DashboardComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  
  public stats = signal<IStatCard[]>([]);
  public activePolls = signal<IActivePoll[]>([]);
  public recentResults = signal<IRecentResult[]>([]);
  public statsLoading = signal(true);
  public activePollsLoading = signal(true);
  public recentResultsLoading = signal(true);
  public error = signal<string | null>(null);

  constructor(
    private router: Router,
    private dashboardService: DashboardService,
  ) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.dashboardService.getStats()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (stats) => {
          this.stats.set(stats);
          this.statsLoading.set(false);
        },
        error: () => {
          this.statsLoading.set(false);
          this.error.set('Failed to load statistics');
        },
      });

    this.dashboardService.getActivePolls()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (polls) => {
          this.activePolls.set(polls);
          this.activePollsLoading.set(false);
        },
        error: () => {
          this.activePollsLoading.set(false);
          this.error.set('Failed to load active polls');
        },
      });

    this.dashboardService.getRecentResults()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (results) => {
          this.recentResults.set(results);
          this.recentResultsLoading.set(false);
        },
        error: () => {
          this.recentResultsLoading.set(false);
          this.error.set('Failed to load recent results');
        },
      });
  }

  public onCreatePoll() {
    this.router.navigate(["/create-poll"]);
  }
}
