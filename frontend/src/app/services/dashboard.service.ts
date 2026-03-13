import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { IStatCard, IActivePoll, IRecentResult } from "../models";
import { IResponseWrapper, IPage, IPollResponse, PollStatus } from "../models/poll.model";
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class DashboardService {
  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/dashboard`;
  private pollsApiUrl = `${environment.apiUrl}/polls`;

  public getAllPolls(page = 0, size = 100): Observable<IResponseWrapper<IPage<IPollResponse>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<IResponseWrapper<IPage<IPollResponse>>>(`${this.pollsApiUrl}`, { params });
  }

  public getStats(): Observable<IStatCard[]> {
    return this.getAllPolls(0, 1000).pipe(
      map(response => {
        if (!response.data || !response.data.content) {
          return [];
        }

        const polls = Array.isArray(response.data.content) ? 
          response.data.content.filter(item => typeof item === 'object' && item !== null) : 
          [];

        const totalPolls = polls.length;
        const activePolls = polls.filter(poll => poll.status === PollStatus.ACTIVE).length;
        const closedPolls = polls.filter(poll => poll.status === PollStatus.CLOSED).length;
        const totalVotes = polls.reduce((sum, poll) => sum + (poll.totalVotes || 0), 0);

        const stats: IStatCard[] = [
          {
            id: '1',
            value: totalPolls.toString(),
            label: 'Total Polls',
            trend: 'up',
            trendValue: '+12%',
            icon: 'lucide:bar-chart-3'
          },
          {
            id: '2', 
            value: activePolls.toString(),
            label: 'Active Polls',
            trend: 'up',
            trendValue: '+8%',
            icon: 'lucide:activity'
          },
          {
            id: '3',
            value: totalVotes.toString(),
            label: 'Total Votes',
            trend: 'up',
            trendValue: '+23%',
            icon: 'lucide:users'
          },
          {
            id: '4',
            value: closedPolls.toString(),
            label: 'Completed Polls',
            trend: 'up',
            trendValue: '+5%',
            icon: 'lucide:check-circle'
          }
        ];

        return stats;
      })
    );
  }

  public getActivePolls(page = 0, size = 10): Observable<{ polls: IActivePoll[], total: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('status', PollStatus.ACTIVE);

    return this.http.get<IResponseWrapper<IPage<IPollResponse>>>(`${this.pollsApiUrl}`, { params })
      .pipe(
        map(response => {
          if (!response.data || !response.data.content) {
            return { polls: [], total: 0 };
          }

          const content = Array.isArray(response.data.content) ? 
            response.data.content.filter(item => typeof item === 'object' && item !== null) : 
            [];

          const polls: IActivePoll[] = content.map(poll => ({
            id: poll.id,
            title: poll.question,
            votes: poll.totalVotes || 0,
            participation: this.calculateParticipation(poll.totalVotes || 0),
            timeLeft: this.calculateTimeLeft(poll.expiresAt),
            timeLeftStatus: this.getTimeLeftStatus(poll.expiresAt)
          }));

          return {
            polls,
            total: response.data.totalElements || 0
          };
        })
      );
  }

  public getRecentResults(): Observable<IRecentResult[]> {
    return this.http.get<IRecentResult[]>(`${this.apiUrl}/recent-results`);
  }

  public recordVote(pollId: string, optionIds: string[]): Observable<IResponseWrapper<null>> {
    return this.http.post<IResponseWrapper<null>>(
      `${this.pollsApiUrl}/${pollId}/vote`,
      { optionIds }
    );
  }

  public getPollById(pollId: string): Observable<IResponseWrapper<IPollResponse>> {
    return this.http.get<IResponseWrapper<IPollResponse>>(`${this.pollsApiUrl}/${pollId}`);
  }

  public refreshPolls(): Observable<IResponseWrapper<IPage<IPollResponse>>> {
    return this.getAllPolls(0, 100);
  }

  private calculateParticipation(totalVotes: number): number {
    // Handle null/undefined values to prevent NaN
    if (!totalVotes || totalVotes === 0) {
      return 0;
    }
    // This is a simplified calculation. You may want to adjust based on your business logic
    // For example, divide by total users or eligible voters
    return Math.min(Math.round((totalVotes / 100) * 100), 100);
  }

  private calculateTimeLeft(expiresAt: string): string {
    const now = new Date().getTime();
    const expiry = new Date(expiresAt).getTime();
    const diff = expiry - now;

    if (diff <= 0) {
      return 'Expired';
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));

    if (days > 0) {
      return `${days} day${days > 1 ? 's' : ''} left`;
    } else if (hours > 0) {
      return `${hours} hour${hours > 1 ? 's' : ''} left`;
    } else {
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      
      return `${minutes} minute${minutes > 1 ? 's' : ''} left`;
    }
  }

  private getTimeLeftStatus(expiresAt: string): 'warning' | 'ok' {
    const now = new Date().getTime();
    const expiry = new Date(expiresAt).getTime();
    const diff = expiry - now;
    const hoursLeft = diff / (1000 * 60 * 60);

    return hoursLeft < 24 ? 'warning' : 'ok';
  }
}
