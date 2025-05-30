import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { User } from '../models/user.model';



@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly apiUrl = 'http://localhost:8080/admin/users'; // Update with your API URL

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
        map(users => users.map(user => ({
            codeUser: user.codeUser,
            username: user.username,
            email: user.email,
            flag: user.flag,
            roles: user.roles
        })))
      );
  }

 // user.service.ts
updateUserRole(userId: number, role: string): Observable<User> {
    return this.http.put<User>(
      `${this.apiUrl}/${userId}/role`, 
      { role }  // Matches @RequestBody Map<String, String> in backend
    );
  }
  
  updateUserStatus(userId: number, isActive: boolean): Observable<User> {
    const endpoint = isActive ? 'activate' : 'deactivate';
    return this.http.put<User>(
      `${this.apiUrl}/${userId}/${endpoint}`, 
      { flag: isActive } ,
      { responseType: 'text' as 'json' } // Include if the backend expects a body
    );
  }

deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}`, {
      responseType: 'text' as 'json' // Handle empty responses
    });
  }
}