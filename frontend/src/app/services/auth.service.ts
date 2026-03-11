import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, tap, map } from "rxjs";
import { ILoginRequest, IRegisterRequest, IAuthResponse } from "../models";

type StoredUser = Omit<IAuthResponse, "token">;

@Injectable({ providedIn: "root" })
export class AuthService {
  private apiUrl = "/api/auth";

  constructor(private http: HttpClient) {}

  public login(email: string, password: string): Observable<IAuthResponse> {
    const request: ILoginRequest = { email, password };

    return this.http.post<{ data: IAuthResponse }>(`${this.apiUrl}/login`, request).pipe(
      tap((res) => {
        if (!res.data) {
          throw new Error("Invalid response: missing data field");
        }

        localStorage.setItem("token", res.data.token);

        const userData: StoredUser = {
          name: res.data.name,
          email: res.data.email,
          role: res.data.role,
        };

        localStorage.setItem("user", JSON.stringify(userData));
      }),
      map((res) => {
        if (!res.data) {
          throw new Error("Invalid response: missing data field");
        }

        return res.data;
      }),
    );
  }

  public register(name: string, email: string, password: string): Observable<IAuthResponse> {
    const request: IRegisterRequest = { name, email, password };

    return this.http.post<{ data: IAuthResponse }>(`${this.apiUrl}/register`, request).pipe(
      tap((res) => {
        if (!res.data) {
          throw new Error("Invalid response: missing data field");
        }

        localStorage.setItem("token", res.data.token);

        const userData: StoredUser = {
          name: res.data.name,
          email: res.data.email,
          role: res.data.role,
        };

        localStorage.setItem("user", JSON.stringify(userData));
      }),
      map((res) => {
        if (!res.data) {
          throw new Error("Invalid response: missing data field");
        }

        return res.data;
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

  public getUser(): StoredUser | null {
    const user = localStorage.getItem("user");

    return user ? JSON.parse(user) : null;
  }
}
