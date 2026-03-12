export interface IPoll {
  id: number;
  question: string;
  options: IPollOption[];
  createdBy: number;
  createdAt: string;
  expiresAt?: string;
  status?: PollStatus;
}

export interface IPollOption {
  id: number;
  text: string;
  votes: number;
}

export interface ICreatePollRequest {
  question: string;
  description: string;
  options: string[];
  multipleChoice: boolean;
  expiresAt: string | number | null;
}

export interface IVoteRequest {
  pollId: number;
  optionId: number;
}

export enum PollStatus {
  ACTIVE = 'ACTIVE',
  CLOSED = 'CLOSED',
  DRAFT = 'DRAFT'
}

export interface IPollResponse {
  id: string;
  question: string;
  description: string;
  creatorName: string;
  hasVoted: boolean;
  status: PollStatus;
  multipleChoice: boolean;
  createdAt: string;
  expiresAt: string;
  totalVotes: number;
  options: IPollOption[];
}

export interface IPageable {
  page: number;
  size: number;
  sort?: string[];
}

export interface IPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface IResponseWrapper<T> {
  httpStatus: string;
  code: number;
  message: string;
  data?: T;
}
