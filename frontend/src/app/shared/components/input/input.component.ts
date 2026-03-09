import { CommonModule } from "@angular/common";
import { ChangeDetectionStrategy, Component, input, signal, forwardRef } from "@angular/core";
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from "@angular/forms";

let nextId = 0;

@Component({
  selector: "app-input",
  imports: [CommonModule],
  templateUrl: "./input.component.html",
  styleUrl: "./input.component.css",
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputComponent),
      multi: true,
    },
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InputComponent implements ControlValueAccessor {
  public label = input("");
  public type = input("text");
  public placeholder = input("");
  public disabled = input(false);
  public error = input(false);
  public success = input(false);
  public errorMessage = input("");
  public inputId = signal(`input-${nextId++}`);

  public value = signal("");

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  public onChange: (value: any) => void = () => {};
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  public onTouched: () => void = () => {};

  public onInput(event: Event) {
    const inputValue = (event.target as HTMLInputElement).value;

    this.value.set(inputValue);
    this.onChange(inputValue);
  }

  public onBlur() {
    this.onTouched();
  }

  public writeValue(val: any): void {
    this.value.set(val || "");
  }

  public registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  public registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  // public setDisabledState(isDisabled: boolean): void {}
}
