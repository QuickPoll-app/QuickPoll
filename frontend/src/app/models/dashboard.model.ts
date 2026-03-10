export interface IStatCard {
  value: string | number;
  label: string;
  icon: string;
  trend: "up" | "down";
  trendValue: string;
}

export interface IActivePoll {
  title: string;
  votes: number;
  participation: number;
  timeLeft: string;
  timeLeftColor: string;
}

export interface IRecentResult {
  title: string;
  winner: string;
  winnerPercentage: number;
  totalVotes: number;
  participation: number;
}