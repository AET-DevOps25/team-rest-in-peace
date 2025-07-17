import { describe, it, expect } from 'vitest';
import { cn } from './utils';

describe('cn utility', () => {
  it('should merge class names correctly', () => {
    const result = cn('class1', 'class2');
    expect(result).toBe('class1 class2');
  });

  it('should handle conditional class names', () => {
    const condition = true;
    const result = cn('class1', condition ? 'class2' : '');
    expect(result).toBe('class1 class2');
  });

  it('should handle array of class names', () => {
    const result = cn(['class1', 'class2']);
    expect(result).toBe('class1 class2');
  });

  it('should handle object notation for conditional classes', () => {
    const result = cn('class1', { class2: true, class3: false });
    expect(result).toBe('class1 class2');
  });

  it('should merge tailwind classes correctly', () => {
    const result = cn('p-4 bg-red-500', 'p-5');
    // tailwind-merge should override p-4 with p-5
    expect(result).toBe('bg-red-500 p-5');
  });
});