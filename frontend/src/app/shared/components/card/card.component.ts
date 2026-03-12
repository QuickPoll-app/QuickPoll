import { ChangeDetectionStrategy, Component, input } from "@angular/core";

@Component({
  selector: "app-card",
  imports: [],
  templateUrl: "./card.component.html",
  styleUrl: "./card.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardComponent {
  voted = input(false);
  expired = input(false);
  hasHeader = input(false);
  hasFooter = input(false);
}
