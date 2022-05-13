import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {RxStompService} from "@stomp/ng2-stompjs";
import {Subscription} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class StompService implements OnInit, OnDestroy {
  public receivedMessages: string[] = [];
  private topicSubscription!: Subscription;

  constructor(private rxStompService: RxStompService) { }

  ngOnInit(): void {
    console.log("init!!");
  }

  connect(): void {
    this.topicSubscription = this.rxStompService
      .watch('/topic/message')
      .subscribe((message) => {
        console.log(message);
        this.receivedMessages.push(message.body);
      });
  }

  send(): void {
    this.rxStompService.publish({destination: '/TTT', body: 'Hello~'})
  }

  ngOnDestroy() {
    this.topicSubscription.unsubscribe();
  }
}
