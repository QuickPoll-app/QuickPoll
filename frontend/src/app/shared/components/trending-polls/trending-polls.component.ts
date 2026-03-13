import {
  Component,
  input,
  output,
  ChangeDetectionStrategy,
  signal,
  computed,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { BadgeComponent } from '../badge/badge.component';
import { IPollResponse } from '../../../models/poll.model';

@Component({
  selector: 'app-trending-polls',
  standalone: true,
  imports: [CommonModule, BadgeComponent],
  templateUrl: './trending-polls.component.html',
  styleUrl: './trending-polls.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrendingPollsComponent {
  public polls = input<IPollResponse[]>([]);
  public loading = input(false);
  public pollClick = output<IPollResponse>();

  public currentPage = signal(1);
  private readonly itemsPerPage = 10;

  public paginatedPolls = computed(() => {
    const allPolls = this.polls();
    const startIndex = (this.currentPage() - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;

    return allPolls.slice(startIndex, endIndex);
  });

  public totalPages = computed(() => {
    return Math.ceil(this.polls().length / this.itemsPerPage);
  });

  public pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: (number | string)[] = [];

    if (total <= 5) {
      for (let i = 1; i <= total; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);

      if (current > 3) {
        pages.push('...');
      }

      const start = Math.max(2, current - 1);
      const end = Math.min(total - 1, current + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (current < total - 2) {
        pages.push('...');
      }

      pages.push(total);
    }

    return pages;
  });

  public onPollClick(poll: IPollResponse): void {
    this.pollClick.emit(poll);
  }

  public getTrendingRank(index: number): string {
    const ranks = ['🥇', '🥈', '🥉'];
    const globalIndex = (this.currentPage() - 1) * this.itemsPerPage + index;
    
    return ranks[globalIndex] || `#${globalIndex + 1}`;
  }

  public getStatusVariant(status: string): 'active' | 'expired' {
    return status === 'ACTIVE' ? 'active' : 'expired';
  }

  public goToPage(page: number | string): void {
    if (typeof page === 'number' && page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  public previousPage(): void {
    if (this.currentPage() > 1) {
      this.currentPage.update(p => p - 1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  public nextPage(): void {
    if (this.currentPage() < this.totalPages()) {
      this.currentPage.update(p => p + 1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }
}
