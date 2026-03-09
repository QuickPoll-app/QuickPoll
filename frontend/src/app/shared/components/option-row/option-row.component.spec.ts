import { ComponentFixture, TestBed } from "@angular/core/testing";

import { OptionRowComponent } from "./option-row.component";

describe("OptionRowComponent", () => {
  let component: OptionRowComponent;
  let fixture: ComponentFixture<OptionRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OptionRowComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(OptionRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
