export interface IPoll {
  id: number;
  question: string;
  options: IPollOption[];
  createdBy: number;
  createdAt: string;
  expiresAt?: string;
}

export interface IPollOption {
  id: number;
  text: string;
  votes: number;
}

export interface ICreatePollRequest {
  question: string;
  options: string[];
  expiresAt?: string;
}

export interface IVoteRequest {
  pollId: number;
  optionId: number;
}
