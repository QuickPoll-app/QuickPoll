import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IUserResponse, IUpdateUserRoleRequest, UserResponse } from '../models/user.model';
import { IResponseWrapper } from '../models/poll.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/users`;

  public getAllUsers(): Observable<IUserResponse[]> {
    return this.http.get<UserResponse | IUserResponse[]>(`${this.apiUrl}`).pipe(
      map(response => {
        if (Array.isArray(response)) {
          return response;
        }
        const resp = response as UserResponse;
        
        if (resp.content && Array.isArray(resp.content)) {
          return resp.content;
        }
        if (resp.data && Array.isArray(resp.data)) {
          return resp.data;
        }
        if (resp.data && typeof resp.data === 'object' && 'content' in resp.data && Array.isArray(resp.data.content)) {
          return resp.data.content;
        }
        return [];
      })
    );
  }

  public getUserById(userId: string): Observable<IResponseWrapper<IUserResponse>> {
    return this.http.get<IResponseWrapper<IUserResponse>>(`${this.apiUrl}/${userId}`);
  }

  public updateUserRole(userId: string, role: 'ADMIN' | 'USER'): Observable<IResponseWrapper<IUserResponse>> {
    const request: IUpdateUserRoleRequest = { role };
    
    return this.http.put<IResponseWrapper<IUserResponse>>(`${this.apiUrl}/${userId}/role-update`, request);
  }

  public deleteUser(userId: string): Observable<IResponseWrapper<null>> {
    return this.http.delete<IResponseWrapper<null>>(`${this.apiUrl}/${userId}`);
  }
}
