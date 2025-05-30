// users-list.component.ts
import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/models/user.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchQuery: string = '';
  isLoading = false;
  errorMessage = '';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = [...users];
        this.isLoading = false;
      },
      error: (err) => {
        this.handleError('Failed to load users', err);
        this.isLoading = false;
      }
    });
  }

  updateRole(user: User): void {
    this.userService.updateUserRole(user.codeUser, user.roles[0]).subscribe({
      error: (err) => this.handleError('Failed to update role', err)
    });
  }

  // Toggle user status with SweetAlert confirmation
  toggleUserStatus(user: User): void {
    Swal.fire({
      title: user.flag ? 'Deactivate User?' : 'Activate User?',
      text: `Are you sure you want to ${user.flag ? 'deactivate' : 'activate'} this user?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, confirm!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        const originalStatus = user.flag;
        const newStatus = !user.flag;

        // Optimistically update UI
        user.flag = newStatus;

        this.userService.updateUserStatus(user.codeUser, newStatus).subscribe({
          next: () => {
            Swal.fire(
              'Updated!',
              `The user has been ${newStatus ? 'activated' : 'deactivated'}.`,
              'success'
            );
          },
          error: (err) => {
            user.flag = originalStatus; // Revert on error
            this.handleError('Failed to update status', err);
          }
        });
      }
    });
  }

  // Delete user with SweetAlert confirmation
  deleteUser(codeUser: number): void {
    Swal.fire({
      title: 'Delete User?',
      text: 'This action cannot be undone!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Yes, delete!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.deleteUser(codeUser).subscribe({
          next: () => {
            this.users = this.users.filter(u => u.codeUser !== codeUser);
            this.filteredUsers = [...this.users];
            Swal.fire('Deleted!', 'The user has been deleted.', 'success');
          },
          error: (err) => this.handleError('Failed to delete user', err)
        });
      }
    });
  }

  applyFilter(): void {
    this.filteredUsers = this.users.filter(user =>
      user.username.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      user.email.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  private handleError(message: string, err?: any): void {
    console.error('Error:', err);
    this.errorMessage = message;
    setTimeout(() => this.errorMessage = '', 3000);
  }
}
