export interface IStatCard {
  id: string | number;
  value: string | number;
  label: string;
  icon: string;
  trend: "up" | "down";
  trendValue: string;
}

export interface IActivePoll {
  id: string | number;
  title: string;
  votes: number;
  participation: number;
  timeLeft: string;
  timeLeftStatus: 'warning' | 'ok';
}

export interface IRecentResult {
  id: string | number;
  title: string;
  winner: string;
  winnerPercentage: number;
  totalVotes: number;
  participation: number;
}
