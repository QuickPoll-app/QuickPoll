import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IStatCard, IActivePoll, IRecentResult } from '../models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = '/api/dashboard';

  private http = inject(HttpClient);

 public getStats(): Observable<IStatCard[]> {
    return this.http.get<IStatCard[]>(`${this.apiUrl}/stats`);
  }

 public getActivePolls(): Observable<IActivePoll[]> {
    return this.http.get<IActivePoll[]>(`${this.apiUrl}/active-polls`);
  }

  public getRecentResults(): Observable<IRecentResult[]> {
    return this.http.get<IRecentResult[]>(`${this.apiUrl}/recent-results`);
  }
}