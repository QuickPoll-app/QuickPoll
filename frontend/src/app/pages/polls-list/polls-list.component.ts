import { Component, ChangeDetectionStrategy, signal, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import {
  PollCardComponent,
  InputComponent,
  FilterTabsComponent,
  ButtonsComponent,
  FilterTab,
} from "../../shared/components";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { Poll } from "../../models";

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
export class PollsListComponent {
  private router = inject(Router);

  public searchQuery = signal("");
  public activeFilter = signal<FilterTab>("all");

  public polls: Poll[] = [
    {
      id: "1",
      title: "Which programming language should we adopt for our next project?",
      description:
        "We're evaluating different programming languages for scalability, team expertise, and long-term maintenance.",
      status: "active",
      type: "single",
      isVoted: false,
      isAnonymous: false,
      votes: 142,
      participation: 78,
      timeLeft: "2d 5h left",
      options: [
        { label: "JavaScript", percentage: 45, color: "rgba(13, 115, 119, 1)" },
        { label: "Python", percentage: 32, color: "rgba(74, 143, 184, 1)" },
      ],
    },
    {
      id: "2",
      title: "What team building activities would you like to see?",
      description:
        "Help us plan engaging team activities for the upcoming quarter. Multiple selections allowed.",
      status: "active",
      type: "multi",
      isVoted: true,
      isAnonymous: true,
      votes: 89,
      participation: 92,
      timeLeft: "5d 12h left",
      options: [
        {
          label: "Outdoor Activities",
          percentage: 67,
          color: "rgba(13, 115, 119, 1)",
        },
        {
          label: "Cooking Class",
          percentage: 54,
          color: "rgba(74, 143, 184, 1)",
        },
      ],
    },
    {
      id: "3",
      title: "Preferred lunch break time for the office?",
      description:
        "Finding the optimal lunch break schedule that works for most team members and office operations.",
      status: "expired",
      type: "single",
      isVoted: true,
      isAnonymous: false,
      votes: 67,
      participation: 85,
      timeLeft: "Expired",
      options: [
        {
          label: "12:00 - 1:00 PM",
          percentage: 58,
          color: "rgba(13, 115, 119, 1)",
        },
        {
          label: "12:30 - 1:30 PM",
          percentage: 42,
          color: "rgba(74, 143, 184, 1)",
        },
      ],
    },
    {
      id: "4",
      title: "Company culture improvements you'd like to see",
      description:
        "Anonymous feedback on areas where we can enhance our workplace culture and employee satisfaction.",
      status: "active",
      type: "multi",
      isVoted: false,
      isAnonymous: true,
      votes: 156,
      participation: 88,
      timeLeft: "3d 2h left",
      options: [
        {
          label: "Better Work-Life Balance",
          percentage: 73,
          color: "rgba(13, 115, 119, 1)",
        },
        {
          label: "Professional Development",
          percentage: 61,
          color: "rgba(74, 143, 184, 1)",
        },
      ],
    },
  ];

  get filteredPolls(): Poll[] {
    let filtered = this.polls;

    if (this.activeFilter() !== "all") {
      if (this.activeFilter() === "active") {
        filtered = filtered.filter((p) => p.status === "active");
      } else if (this.activeFilter() === "closed") {
        filtered = filtered.filter((p) => p.status === "expired");
      } else if (this.activeFilter() === "voted") {
        filtered = filtered.filter((p) => p.isVoted);
      }
    }

    if (this.searchQuery()) {
      const query = this.searchQuery().toLowerCase();

      filtered = filtered.filter(
        (p) => p.title.toLowerCase().includes(query) || p.description.toLowerCase().includes(query),
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

  public onPollClick(pollId: string) {
    const poll = this.polls.find((p) => p.id === pollId);

    if (!poll) return;

    if (poll.status === "expired") {
      this.router.navigate(["/poll", pollId, "results"]);
    } else if (poll.isVoted) {
      this.router.navigate(["/poll", pollId, "results"]);
    } else {
      const route = poll.type === "single" ? "vote-single" : "vote-multi";

      this.router.navigate(["/poll", pollId, route]);
    }
  }

  public onCreatePoll() {
    this.router.navigate(["/create-poll"]);
  }
}
