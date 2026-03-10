import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, tap, map } from "rxjs";
import { ILoginRequest, IRegisterRequest, IAuthResponse } from "../models";

@Injectable({ providedIn: "root" })
export class AuthService {
  private apiUrl = "/api/auth";

  constructor(private http: HttpClient) {}

  public login(email: string, password: string): Observable<IAuthResponse> {
    const request: ILoginRequest = { email, password };

    return this.http.post<{ data: IAuthResponse }>(`${this.apiUrl}/login`, request).pipe(
      tap((res) => {
        if (res.data) {
          localStorage.setItem("token", res.data.token);
          localStorage.setItem("user", JSON.stringify(res.data));
        }
      }),
      map((res) => res.data)
    );
  }

  public register(name: string, email: string, password: string): Observable<IAuthResponse> {
    const request: IRegisterRequest = { name, email, password };

    return this.http.post<{ data: IAuthResponse }>(`${this.apiUrl}/register`, request).pipe(
      tap((res) => {
        if (res.data) {
          localStorage.setItem("token", res.data.token);
          localStorage.setItem("user", JSON.stringify(res.data));
        }
      }),
      map((res) => res.data)
    );
  }

  public logout(): void {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  }

  public isLoggedIn(): boolean {
    return !!localStorage.getItem("token");
  }

  public getToken(): string | null {
    return localStorage.getItem("token");
  }

  public getUser(): { name: string; email: string; role: string } | null {
    const user = localStorage.getItem("user");

    return user ? JSON.parse(user) : null;
  }
}
