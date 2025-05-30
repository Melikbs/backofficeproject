/*import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import SockJS from 'sockjs-client';

@Injectable({
    providedIn: 'root'
  })
export class NotificationService {
  private stompClient: Client;
  
  private notificationSubject = new Subject<string>();
  public notification$ = this.notificationSubject.asObservable();
  constructor() {
    this.stompClient = new Client({
      brokerURL: undefined, // obligatoire pour utiliser SockJS
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        console.log('Connected:', frame);

        // Abonnement au topic de notifications
        this.stompClient.subscribe('/topic/notifications', (message) => {
            console.log('Notification reçue:', message.body);
            this.notificationSubject.next(message.body);
          });
          
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame);
      }
    });

    this.stompClient.activate(); // ✅ au lieu de connect()
  }

  disconnect() {
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.deactivate(); // ✅ au lieu de disconnect()
    }
  }
  connect() {
    if (this.stompClient && !this.stompClient.active) {
      this.stompClient.activate(); // démarre la connexion
    }
  }
  
}
*/