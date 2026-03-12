export interface IVotePollOption {
  id: string;
  label: string;
  description: string;
}

export interface Poll {
  id: string;
  title: string;
  description: string;
  status: "active" | "expired";
  type: "single" | "multi";
  isVoted: boolean;
  isAnonymous: boolean;
  votes: number;
  participation: number;
  timeLeft: string;
  options: { label: string; percentage: number; color: string }[];
}
