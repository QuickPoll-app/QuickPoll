import { Component, ChangeDetectionStrategy, signal, inject, OnInit, DestroyRef } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import {
  PollCardComponent,
  InputComponent,
  FilterTabsComponent,
  ButtonsComponent,
  FilterTab,
} from "../../shared/components";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { IPollResponse } from "../../models/poll.model";
import { DashboardService } from "../../services/dashboard.service";

@Component({
  selector: "app-polls-list",
  standalone: true,
  templateUrl: "./polls-list.component.html",
  styleUrl: "./polls-list.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    FormsModule,
    PollCardComponent,
    InputComponent,
    ButtonsComponent,
    FilterTabsComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PollsListComponent implements OnInit {
  private router = inject(Router);
  private dashboardService = inject(DashboardService);
  private destroyRef = inject(DestroyRef);

  public searchQuery = signal("");
  public activeFilter = signal<FilterTab>("all");
  public polls = signal<IPollResponse[]>([]);
  public loading = signal(true);

  get filteredPolls(): IPollResponse[] {
    let filtered = this.polls();

    if (this.activeFilter() !== "all") {
      if (this.activeFilter() === "active") {
        filtered = filtered.filter((p) => p.status === "ACTIVE");
      } else if (this.activeFilter() === "closed") {
        filtered = filtered.filter((p) => p.status === "CLOSED");
      } else if (this.activeFilter() === "voted") {
        filtered = filtered.filter((p) => p.HasVoted);
      }
    }

    if (this.searchQuery()) {
      const query = this.searchQuery().toLowerCase();
      
      filtered = filtered.filter(
        (p) => p.question.toLowerCase().includes(query) || p.description.toLowerCase().includes(query),
      );
    }

    return filtered;
  }

  public onSearch(event: Event | string) {
    const query = typeof event === "string" ? event : (event.target as HTMLInputElement).value;

    this.searchQuery.set(query || "");
  }

  public onFilterChange(filter: FilterTab) {
    this.activeFilter.set(filter);
  }

  public onPollClick(poll: IPollResponse) {
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

  ngOnInit() {
    this.loading.set(true);
    this.dashboardService.getAllPolls(0, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.polls.set(response.data?.content || []);
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Error fetching polls:', error);
          this.loading.set(false);
        }
      });
  }
}
