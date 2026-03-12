import { Injectable, signal, effect } from "@angular/core";

@Injectable({ providedIn: "root" })
export class ThemeService {
  public isDarkMode = signal<boolean>(false);

  constructor() {
    const savedTheme = localStorage.getItem("theme");
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;

    this.isDarkMode.set(savedTheme === "dark" || (!savedTheme && prefersDark));

    effect(() => {
      document.documentElement.setAttribute("data-theme", this.isDarkMode() ? "dark" : "light");
      localStorage.setItem("theme", this.isDarkMode() ? "dark" : "light");
    });
  }

  public toggleTheme(): void {
    this.isDarkMode.update((v) => !v);
  }
}
