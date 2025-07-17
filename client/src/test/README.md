# Testing with Vitest

This project uses [Vitest](https://vitest.dev/) for unit testing.

## Running Tests

To run all tests once:

```bash
npm run test
```

To run tests in watch mode (tests will re-run when files change):

```bash
npm run test:watch
```

## Test Structure

Tests are located next to the files they test with a `.test.tsx` or `.test.ts` extension.

For example:
- `src/components/ui/button.tsx` has tests in `src/components/ui/button.test.tsx`
- `src/lib/utils.ts` has tests in `src/lib/utils.test.ts`

## Writing Tests

### Component Tests

For React components, we use `@testing-library/react` to render components and query the DOM.

Example:

```tsx
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Button } from './button';

describe('Button component', () => {
  it('renders correctly', () => {
    render(<Button>Click me</Button>);
    const button = screen.getByRole('button', { name: /click me/i });
    expect(button).toBeInTheDocument();
  });
});
```

### Utility Tests

For utility functions, we test the input and output directly.

Example:

```ts
import { describe, it, expect } from 'vitest';
import { cn } from './utils';

describe('cn utility', () => {
  it('should merge class names correctly', () => {
    const result = cn('class1', 'class2');
    expect(result).toBe('class1 class2');
  });
});
```

## Test Setup

The global test setup is in `src/test/setup.ts`. This file imports `@testing-library/jest-dom` to add custom DOM matchers to Vitest.