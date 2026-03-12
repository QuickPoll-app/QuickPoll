import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PollVoteMultiComponent } from './poll-vote-multi.component';

describe('PollVoteMultiComponent', () => {
  let component: PollVoteMultiComponent;
  let fixture: ComponentFixture<PollVoteMultiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PollVoteMultiComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PollVoteMultiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle option selection', () => {
    component.onOptionToggle('escape-room');
    expect(component.isOptionSelected('escape-room')).toBe(true);
  });

  it('should deselect option when toggled again', () => {
    component.onOptionToggle('cooking-class');
    component.onOptionToggle('cooking-class');
    expect(component.isOptionSelected('cooking-class')).toBe(false);
  });

  it('should allow multiple selections', () => {
    component.onOptionToggle('escape-room');
    component.onOptionToggle('cooking-class');
    expect(component.selectedOptions().size).toBe(2);
  });

  it('should submit vote when options are selected', () => {
    component.onOptionToggle('trivia-night');
    component.onSubmitVote();
    expect(component.isSubmitting()).toBe(true);
  });

  it('should not submit vote when no options are selected', () => {
    component.onSubmitVote();
    expect(component.isSubmitting()).toBe(false);
  });
});
