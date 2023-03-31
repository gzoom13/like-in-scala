import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, exhaustMap, catchError } from 'rxjs/operators';
import { counterLoaded } from './counter.actions';
import { CounterService } from './counter.service';

@Injectable()
export class CounterEffects {

  loadCounter$ = createEffect(() =>
    this.actions$.pipe(
      ofType('[Counter API] Fetch Request'),
      exhaustMap(() => this.counterService.load()
        .pipe(
          map(counter => counterLoaded({counter})),
          catchError(() => of({ type: '[Counter API] Counter Loaded Error' }))
        )
      )
    )
  );

  constructor(
    private actions$: Actions,
    private counterService: CounterService
  ) {}
}