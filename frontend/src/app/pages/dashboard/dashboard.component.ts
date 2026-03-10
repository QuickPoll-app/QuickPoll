import { Component, ChangeDetectionStrategy, OnInit, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { IStatCard, IActivePoll, IRecentResult } from "../../models";
import { DashboardService } from "../../services/dashboard.service";
import {
  StatCardComponent,
  BadgeComponent,
  ButtonsComponent,
  CardComponent,
  ProgressBarComponent,
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
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class DashboardComponent implements OnInit {
  public stats = signal<IStatCard[]>([]);
  public activePolls = signal<IActivePoll[]>([]);
  public recentResults = signal<IRecentResult[]>([]);

  constructor(
    private router: Router,
    private dashboardService: DashboardService,
  ) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.dashboardService.getStats().subscribe((stats) => this.stats.set(stats));
    this.dashboardService.getActivePolls().subscribe((polls) => this.activePolls.set(polls));
    this.dashboardService
      .getRecentResults()
      .subscribe((results) => this.recentResults.set(results));
  }

  public onCreatePoll() {
    this.router.navigate(["/create-poll"]);
  }
}
