import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {RxStompService} from "@stomp/ng2-stompjs";
import {Subscription} from "rxjs";
import {Symbol} from "../classes/Symbol";

@Injectable({
  providedIn: 'root'
})
export class TradeStompService implements OnInit, OnDestroy {
  public tradesMap: Map<string, Trade[]> = new Map(Object.keys(Symbol).map(k => [k, []]))
  private subscription!: Subscription;

  constructor(private rxStompService: RxStompService) { }

  ngOnInit(): void {
    console.log("init!!");
  }

  connect(symbol: string): void {
    this.subscription = this.rxStompService
      .watch('/topic/message/' + symbol.toUpperCase())
      .subscribe((message) => {
        console.log(message.body);
        let tradeJson = JSON.parse(message.body);
        let trade = new Trade(tradeJson.symbol, tradeJson.price, tradeJson.timestamp);
        this.tradesMap.get(symbol.toUpperCase())!.push(trade);
      });
  }

  send(): void {
    this.rxStompService.publish({destination: '/TTT', body: 'Hello~'})
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}

class Trade {
  constructor(public symbol: string,
              public price: number,
              public timestamp: number) {
  }
}
