import {
  Component,
  input,
  OnInit,
  ChangeDetectionStrategy,
  inject,
  PLATFORM_ID,
  ChangeDetectorRef,
  effect,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ChartModule } from 'primeng/chart';
import { IOptionResponse, IChartData, IChartOptions } from '../../../models/poll.model';

@Component({
  selector: 'app-pie-chart',
  template: `
    <div class="card flex justify-center">
      <p-chart
        type="doughnut"
        [data]="chartData"
        [options]="chartOptions"
        class="w-full md:w-[30rem]"
      />
    </div>
  `,
  standalone: true,
  imports: [CommonModule, ChartModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PieChartComponent implements OnInit {
  public options = input<IOptionResponse[]>([]);

 public chartData: IChartData | null = null;
  public chartOptions: IChartOptions | null = null;

  private platformId = inject(PLATFORM_ID);
  private cd = inject(ChangeDetectorRef);

  private readonly colors: string[] = [
    '#06B6D4',
    '#F97316',
    '#22C55E',
    '#EF4444',
    '#A855F7',
    '#EC4899',
    '#6366F1',
    '#14B8A6',
  ];

  private readonly hoverColors: string[] = [
    '#0891B2',
    '#EA580C',
    '#16A34A',
    '#DC2626',
    '#9333EA',
    '#DB2777',
    '#4F46E5',
    '#0D9488',
  ];

  constructor() {
    effect(() => {
      if (this.options().length > 0) {
        this.initChart();
      }
    });
  }

  ngOnInit(): void {
    this.initChart();
  }

  private initChart(): void {
    if (isPlatformBrowser(this.platformId)) {
      const documentStyle = getComputedStyle(document.documentElement);
      const textColor = documentStyle.getPropertyValue('--p-text-color') || '#000000';

      const labels = this.options().map((opt) => opt.text);
      const data = this.options().map((opt) => opt.voteCount);
      const backgroundColor = this.options().map((_, idx) =>
        this.colors[idx % this.colors.length]
      );
      const hoverBackgroundColor = this.options().map((_, idx) =>
        this.hoverColors[idx % this.hoverColors.length]
      );

      this.chartData = {
        labels,
        datasets: [
          {
            data,
            backgroundColor,
            hoverBackgroundColor,
            borderColor: '#ffffff',
            borderWidth: 2,
          },
        ],
      };

      this.chartOptions = {
        cutout: '60%',
        plugins: {
          legend: {
            labels: {
              color: textColor,
              font: {
                size: 14,
              },
              padding: 15,
            },
          },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            titleColor: '#ffffff',
            bodyColor: '#ffffff',
            borderColor: '#ffffff',
            borderWidth: 1,
          },
        },
        maintainAspectRatio: true,
        responsive: true,
      };

      this.cd.markForCheck();
    }
  }
}
