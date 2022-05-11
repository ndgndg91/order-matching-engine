import { Injectable } from '@angular/core';
import {MatchedPriceMessage} from "../classes/MatchedPriceMessage";
import {StompRService} from "@stomp/ng2-stompjs";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  webSocket: WebSocket | undefined;
  messages: MatchedPriceMessage[] = [];

  public constructor() {}

  openWebSocket(): void {
    this.webSocket = new WebSocket('ws://localhost:7070/ws-stomp');
    this.webSocket.onopen = (event) => {
      console.log(`open`);
      console.log(event);
    };

    this.webSocket.onmessage = (event) => {
      const message = JSON.parse(event.data);
      this.messages.push(message);
    };

    this.webSocket.onclose = (event) => {
      console.log(`close : ${event}`);
    };

    this.webSocket.onerror = (event) => {
      console.error(event);
    };
  }

  closeWebSocket(): void {
    this.messages = [];
    if (this.webSocket !== undefined) this.webSocket.close();
  }
}
