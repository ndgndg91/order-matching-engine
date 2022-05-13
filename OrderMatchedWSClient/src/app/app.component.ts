import {Component, OnInit} from '@angular/core';
import {WebSocketService} from "./service/web-socket-service";
import {StompService} from "./service/stomp-service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'OrderMatchedWSClient';

  constructor(public webSocketService: WebSocketService, public stompService: StompService) {}

  ngOnInit(): void {
  }
}
