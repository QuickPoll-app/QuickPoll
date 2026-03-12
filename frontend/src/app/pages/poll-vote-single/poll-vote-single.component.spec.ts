import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PollVoteSingleComponent } from './poll-vote-single.component';

describe('PollVoteSingleComponent', () => {
  let component: PollVoteSingleComponent;
  let fixture: ComponentFixture<PollVoteSingleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PollVoteSingleComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PollVoteSingleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should select an option', () => {
    component.onOptionSelect('javascript');
    expect(component.selectedOption()).toBe('javascript');
  });

  it('should submit vote when option is selected', () => {
    component.onOptionSelect('python');
    component.onSubmitVote();
    expect(component.isSubmitting()).toBe(true);
  });

  it('should not submit vote when no option is selected', () => {
    component.onSubmitVote();
    expect(component.isSubmitting()).toBe(false);
  });
});
