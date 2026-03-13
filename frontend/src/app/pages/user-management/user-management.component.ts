import {
  Component,
  ChangeDetectionStrategy,
  signal,
  inject,
  OnInit,
  DestroyRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { IUserResponse } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { BadgeComponent, ButtonsComponent, CardComponent } from '../../shared/components';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [CommonModule, FormsModule, BadgeComponent, ButtonsComponent, CardComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class UserManagementComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  public users = signal<IUserResponse[]>([]);
  public loading = signal(true);
  public error = signal<string | null>(null);
  public deleteConfirmation = signal<string | null>(null);
  public editingUserId = signal<string | null>(null);
  public editingRole = signal<'ADMIN' | 'USER' | null>(null);
  public isAdmin = signal(false);

  constructor() {
    const user = this.authService.getUser();

    this.isAdmin.set(user?.role?.toLowerCase() === 'admin');

    if (!this.isAdmin()) {
      this.router.navigate(['/dashboard']);
    }
  }

  ngOnInit() {
    this.loadUsers();
  }

  private loadUsers(): void {
    this.loading.set(true);
    this.error.set(null);

    this.userService
      .getAllUsers()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (users) => {
          this.users.set(users);
          this.loading.set(false);
        },
        error: (error) => {
          console.error('Error loading users:', error);
          this.error.set('Failed to load users');
          this.loading.set(false);
        },
      });
  }

  public onEditRole(user: IUserResponse): void {
    this.editingUserId.set(user.id);
    this.editingRole.set(user.role);
  }

  public onCancelEdit(): void {
    this.editingUserId.set(null);
    this.editingRole.set(null);
  }

  public onSaveRole(userId: string): void {
    const newRole = this.editingRole();
    
    if (!newRole) return;

    this.userService
      .updateUserRole(userId, newRole)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.editingUserId.set(null);
          this.editingRole.set(null);
          this.loadUsers();
        },
        error: (error) => {
          console.error('Error updating user role:', error);
          this.error.set('Failed to update user role');
        },
      });
  }

  public onDeleteUser(user: IUserResponse): void {
    this.deleteConfirmation.set(user.id);
  }

  public confirmDelete(userId: string): void {
    this.userService
      .deleteUser(userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.deleteConfirmation.set(null);
          this.loadUsers();
        },
        error: (error) => {
          console.error('Error deleting user:', error);
          this.error.set('Failed to delete user');
          this.deleteConfirmation.set(null);
        },
      });
  }

  public cancelDelete(): void {
    this.deleteConfirmation.set(null);
  }

  public getRoleVariant(role: string): 'active' | 'expired' {
    return role === 'ADMIN' ? 'active' : 'expired';
  }

  public formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
