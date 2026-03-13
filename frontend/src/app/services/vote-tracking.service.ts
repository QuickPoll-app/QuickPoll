import { Injectable } from '@angular/core';
import { signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VoteTrackingService {
  private votedPollIds = signal<Set<string>>(new Set());

  constructor() {
    this.loadVotedPolls();
  }

  public markAsVoted(pollId: string): void {
    const updated = new Set(this.votedPollIds());
    
    updated.add(pollId);
    this.votedPollIds.set(updated);
    this.saveVotedPolls();
  }

  public hasVoted(pollId: string): boolean {
    return this.votedPollIds().has(pollId);
  }

  public getVotedPollIds(): Set<string> {
    return this.votedPollIds();
  }

  private saveVotedPolls(): void {
    const ids = Array.from(this.votedPollIds());

    localStorage.setItem('votedPollIds', JSON.stringify(ids));
  }

  private loadVotedPolls(): void {
    const stored = localStorage.getItem('votedPollIds');

    if (stored) {
      try {
        const ids = JSON.parse(stored);

        this.votedPollIds.set(new Set(ids));
      } catch (e) {
        console.error('Error loading voted polls:', e);
      }
    }
  }
}
