import { ChangeDetectionStrategy, Component, input, output, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-modal",
  imports: [CommonModule],
  templateUrl: "./modal.component.html",
  styleUrl: "./modal.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ModalComponent {
  public isOpen = input.required<boolean>();
  public title = input<string>('');
  public showCloseButton = input(true);
  public closeOnOverlayClick = input(true);
  
  public closed = output<void>();

  public onOverlayClick() {
    if (this.closeOnOverlayClick()) {
      this.closed.emit();
    }
  }

  public onClose() {
    this.closed.emit();
  }
}
