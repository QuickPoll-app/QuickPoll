import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ICreatePollRequest, IResponseWrapper, IPollResponse, IPage } from "../models/poll.model";
import { environment } from "../../environments/environment";

@Injectable({ providedIn: "root" })
export class PollService {
  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/polls`;

  public getAllPolls(page = 0, size = 10): Observable<IPage<IPollResponse>> {
    return this.http.get<IPage<IPollResponse>>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  public getPollById(id: number): Observable<IResponseWrapper<IPollResponse>> {
    return this.http.get<IResponseWrapper<IPollResponse>>(`${this.apiUrl}/${id}`);
  }

  public createPoll(poll: ICreatePollRequest): Observable<IResponseWrapper<IPollResponse>> {
    return this.http.post<IResponseWrapper<IPollResponse>>(`${this.apiUrl}`, poll);
  }

  public deletePoll(pollId: string): Observable<IResponseWrapper<null>> {
    return this.http.delete<IResponseWrapper<null>>(`${this.apiUrl}/${pollId}`);
  }

  // TODO: Implement vote method
  // vote(pollId: number, optionIds: number[]): Observable<any> { ... }

  // TODO: Implement close poll method
}
