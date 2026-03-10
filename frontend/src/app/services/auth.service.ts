import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, tap } from "rxjs";
import { ILoginRequest, IRegisterRequest, IAuthResponse } from "../models";

@Injectable({ providedIn: "root" })
export class AuthService {
  private apiUrl = "/api/auth";

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<IAuthResponse> {
    const request: ILoginRequest = { email, password };

    return this.http.post<IAuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((res) => {
        localStorage.setItem("token", res.token);
        localStorage.setItem(
          "user",
          JSON.stringify({ name: res.name, email: res.email, role: res.role }),
        );
      }),
    );
  }

  public register(name: string, email: string, password: string): Observable<IAuthResponse> {
    const request: IRegisterRequest = { name, email, password };

    return this.http.post<IAuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap((res) => {
        localStorage.setItem("token", res.token);
        localStorage.setItem(
          "user",
          JSON.stringify({ name: res.name, email: res.email, role: res.role }),
        );
      }),
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
    const u = localStorage.getItem("user");

    return u ? JSON.parse(u) : null;
  }
}
