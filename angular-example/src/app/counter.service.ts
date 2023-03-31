import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class CounterService {
    constructor(private http: HttpClient) { }

    load(): Observable<number> {
        return this.http.get<String>('https://www.random.org/integers/?num=1&min=1&max=3000&col=1&base=10&format=plain&rnd=new')
            .pipe(map(s => +s));
    }
}