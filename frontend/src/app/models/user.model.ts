export interface IUser {
  id: number;
  email: string;
  name: string;
  role: "ADMIN" | "USER";
}

export interface IUserResponse {
  id: string;
  email: string;
  fullName: string;
  role: "ADMIN" | "USER";
  createdAt: string;
  updatedAt: string;
}

export interface IUpdateUserRoleRequest {
  role: "ADMIN" | "USER";
}

export interface UserResponse {
  content?: IUserResponse[];
  data?: IUserResponse[] | { content?: IUserResponse[] };
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
