import { Component, ChangeDetectionStrategy, signal, computed, effect, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ActivatedRoute, Router } from "@angular/router";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import {
  ResultsBarComponent,
  WinnerCalloutComponent,
  SidebarComponent,
} from "../../shared/components";
import { INavItem, IUserProfile } from "../../shared/components/sidebar/sidebar.component";

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
  closedDate: string;
  totalVotes: number;
  participation: number;
  options: PollOption[];
}

@Component({
  selector: "app-poll-expired",
  templateUrl: "./poll-expired.component.html",
  styleUrl: "./poll-expired.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, ResultsBarComponent, WinnerCalloutComponent, SidebarComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PollExpiredComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  public chartView = signal<"bar" | "pie">("bar");
  public Math = Math;

  public navItems = signal<INavItem[]>([
    { label: "Dashboard", icon: "lucide:layout-dashboard", route: "/dashboard" },
    { label: "Polls", icon: "lucide:vote", route: "/polls" },
    { label: "Create Poll", icon: "lucide:plus-circle", route: "/create-poll" },
  ]);

  public userProfile = signal<IUserProfile>({
    name: "John Smith",
    role: "Admin",
    avatar:
      "https://static.paraflowcontent.com/public/resource/image/e5e9e918-8999-4080-9468-b04db423bd05.jpeg",
  });

  public poll = signal<Poll>({
    id: "1",
    title: "Programming language for next project",
    description:
      "Help us decide which programming language to use for our upcoming web application. Consider factors like team expertise, project requirements, and long-term maintainability.",
    type: "Single Choice",
    closedDate: "March 15, 2024",
    totalVotes: 142,
    participation: 73,
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

  public onBackClick() {
    this.router.navigate(["/polls"]);
  }

  public onExport() {
    // Export functionality
  }
}
