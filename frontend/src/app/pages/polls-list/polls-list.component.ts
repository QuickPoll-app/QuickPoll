import { Component, ChangeDetectionStrategy, signal, inject, OnInit, AfterViewInit, DestroyRef } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router, NavigationEnd } from "@angular/router";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { filter } from "rxjs/operators";
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
import { VoteTrackingService } from "../../services/vote-tracking.service";
import { AuthService } from "../../services/auth.service";

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
export class PollsListComponent implements OnInit, AfterViewInit {
  private router = inject(Router);
  private dashboardService = inject(DashboardService);
  private voteTrackingService = inject(VoteTrackingService);
  private authService = inject(AuthService);
  private destroyRef = inject(DestroyRef);

  public searchQuery = signal("");
  public activeFilter = signal<FilterTab>("all");
  public polls = signal<IPollResponse[]>([]);
  public loading = signal(true);
  public isAdmin = signal(false);
  public searchLoading = signal(false);
  public searchError = signal<string | null>(null);
  public searchInputValue = signal("");

  constructor() {
    const user = this.authService.getUser();
    
    this.isAdmin.set(user?.role?.toLowerCase() === 'admin');
  }

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
    
    this.searchInputValue.set(query || "");
    this.searchQuery.set(query || "");
  }

  public onSearchById() {
    const searchTerm = this.searchInputValue().trim().toLowerCase();

    if (!searchTerm) {
      this.searchError.set('Please enter a poll name');
      return;
    }

    this.searchLoading.set(true);
    this.searchError.set(null);

    this.dashboardService.getAllPolls(0, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.searchLoading.set(false);
          const polls = response.data?.content || [];
          const foundPoll = polls.find(p => p.question.toLowerCase().includes(searchTerm));
          
          if (foundPoll) {
            const poll = {
              ...foundPoll,
              HasVoted: foundPoll.HasVoted || this.voteTrackingService.hasVoted(foundPoll.id)
            };

            this.searchInputValue.set('');

            this.onPollClick(poll);
          } else {
            this.searchError.set('Poll not found');
          }
        },
        error: (error) => {
          this.searchLoading.set(false);
          this.searchError.set(error?.error?.message || 'Error searching for poll');
        }
      });
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

  private loadPolls() {
    this.loading.set(true);
    this.dashboardService.getAllPolls(0, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          const polls = response.data?.content || [];
          const updatedPolls = polls.map(p => ({
            ...p,
            HasVoted: p.HasVoted || this.voteTrackingService.hasVoted(p.id)
          }));

          this.polls.set(updatedPolls);
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Error fetching polls:', error);
          this.loading.set(false);
        }
      });
  }

  ngOnInit() {
    this.loadPolls();
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        if (this.router.url === '/polls') {
          this.loadPolls();
        }
      });
  }

  ngAfterViewInit() {
    const focusListener = () => this.loadPolls();

    window.addEventListener('focus', focusListener);
    
    this.destroyRef.onDestroy(() => {
      window.removeEventListener('focus', focusListener);
    });
  }
}
