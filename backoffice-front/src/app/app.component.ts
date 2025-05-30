import { Component, OnInit } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
//import { NotificationService } from './services/notification.service';



@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    
    
    
],

  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent  {
  title = 'backoffice-front';
  constructor() {}
}
/*ngOnInit() {
  this.notificationService.connect();
    this.notificationService.notification$.subscribe((message) => {
      console.log('New Notification:', message);
    });
  }
}*/


 
