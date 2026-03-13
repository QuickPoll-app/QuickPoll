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
  ACTIVE = "ACTIVE",
  CLOSED = "CLOSED",
  DRAFT = "DRAFT",
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

export interface IOptionResponse {
  id: string;
  text: string;
  voteCount: number;
  percentage: number;
}

export interface IPollResultsResponse {
  id: string;
  question: string;
  description: string;
  creatorName: string;
  HasVoted: boolean;
  status: string;
  multipleChoice: boolean;
  createdAt: string;
  expiresAt: string;
  totalVotes: number;
  participationRate: number;
  options: IOptionResponse[];
}

export interface IChartDataset {
  data: number[];
  backgroundColor: string[];
  hoverBackgroundColor: string[];
  borderColor: string;
  borderWidth: number;
}

export interface IChartData {
  labels: string[];
  datasets: IChartDataset[];
}

export interface ILegendLabels {
  color: string;
  font: {
    size: number;
  };
  padding: number;
}

export interface ITooltipConfig {
  backgroundColor: string;
  titleColor: string;
  bodyColor: string;
  borderColor: string;
  borderWidth: number;
}

export interface IChartOptions {
  cutout: string;
  plugins: {
    legend: {
      labels: ILegendLabels;
    };
    tooltip: ITooltipConfig;
  };
  maintainAspectRatio: boolean;
  responsive: boolean;
}

export interface ITrendingPoll extends IPollResponse {
  trendingScore?: number;
}
