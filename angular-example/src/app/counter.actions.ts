import { createAction, props } from '@ngrx/store';

export const increment = createAction('[Counter Component] Increment');
export const decrement = createAction('[Counter Component] Decrement');
export const reset = createAction('[Counter Component] Reset');
export const loadCounter = createAction('[Counter API] Fetch Request');
export const counterLoaded = createAction('[Counter API] Counter Loaded Success', props<{ counter: number }>());
export const counterLoadingError = createAction('[Counter API] Counter Loaded Error');
