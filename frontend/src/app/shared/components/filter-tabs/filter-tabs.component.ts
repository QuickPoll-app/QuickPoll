import { Component, ChangeDetectionStrategy, input, output } from "@angular/core";
import { CommonModule } from "@angular/common";

export type FilterTab = "all" | "active" | "closed" | "voted";

@Component({
  selector: "app-filter-tabs",
  standalone: true,
  templateUrl: "./filter-tabs.component.html",
  styleUrl: "./filter-tabs.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
})
export class FilterTabsComponent {
  public activeTab = input<FilterTab>("all");
  public tabChanged = output<FilterTab>();

  public tabs: { label: string; value: FilterTab }[] = [
    { label: "All", value: "all" },
    { label: "Active", value: "active" },
    { label: "Closed", value: "closed" },
    { label: "Voted", value: "voted" },
  ];

  public selectTab(tab: FilterTab) {
    this.tabChanged.emit(tab);
  }
}
