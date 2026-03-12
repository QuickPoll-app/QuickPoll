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
  id: string;
  text: string;
  voteCount: number;
  percentage: number;
}

export interface ICreatePollRequest {
  question: string;
  description: string;
  options: string[];
  multipleChoice: boolean;
  expiresAt: string | null;
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
  HasVoted: boolean;
  status: PollStatus;
  multipleChoice: boolean;
  createdAt: string;
  expiresAt: string;
  totalVotes: number;
  participationRate: number;
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
