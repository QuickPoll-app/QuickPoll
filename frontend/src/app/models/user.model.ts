export interface IUser {
  id: number;
  email: string;
  name: string;
  role: "ADMIN" | "USER";
}

export interface ILoginRequest {
  email: string;
  password: string;
}

export interface IRegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface IAuthResponse {
  token: string;
  email: string;
  name: string;
  role: "ADMIN" | "USER";
}
