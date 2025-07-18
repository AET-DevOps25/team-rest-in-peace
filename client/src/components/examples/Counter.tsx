import { useState } from 'react';
import { Button } from '../ui/button';

interface CounterProps {
  initialCount?: number;
  step?: number;
}

export function Counter({ initialCount = 0, step = 1 }: CounterProps) {
  const [count, setCount] = useState(initialCount);

  const increment = () => setCount(count + step);
  const decrement = () => setCount(count - step);
  const reset = () => setCount(initialCount);

  return (
    <div className="flex flex-col items-center gap-4 p-4 border rounded-lg">
      <h2 className="text-xl font-bold">Counter: {count}</h2>
      <div className="flex gap-2">
        <Button variant="outline" onClick={decrement} aria-label="Decrement">
          -
        </Button>
        <Button variant="outline" onClick={reset} aria-label="Reset">
          Reset
        </Button>
        <Button variant="outline" onClick={increment} aria-label="Increment">
          +
        </Button>
      </div>
    </div>
  );
}