import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Counter } from './Counter';

describe('Counter component', () => {
  it('renders with default initial count of 0', () => {
    render(<Counter />);
    expect(screen.getByText('Counter: 0')).toBeInTheDocument();
  });

  it('renders with provided initial count', () => {
    render(<Counter initialCount={10} />);
    expect(screen.getByText('Counter: 10')).toBeInTheDocument();
  });

  it('increments the count when + button is clicked', () => {
    render(<Counter />);
    const incrementButton = screen.getByRole('button', { name: /increment/i });
    
    fireEvent.click(incrementButton);
    expect(screen.getByText('Counter: 1')).toBeInTheDocument();
    
    fireEvent.click(incrementButton);
    expect(screen.getByText('Counter: 2')).toBeInTheDocument();
  });

  it('decrements the count when - button is clicked', () => {
    render(<Counter initialCount={5} />);
    const decrementButton = screen.getByRole('button', { name: /decrement/i });
    
    fireEvent.click(decrementButton);
    expect(screen.getByText('Counter: 4')).toBeInTheDocument();
    
    fireEvent.click(decrementButton);
    expect(screen.getByText('Counter: 3')).toBeInTheDocument();
  });

  it('resets the count to initial value when Reset button is clicked', () => {
    render(<Counter initialCount={5} />);
    const incrementButton = screen.getByRole('button', { name: /increment/i });
    const resetButton = screen.getByRole('button', { name: /reset/i });
    
    // First increment a few times
    fireEvent.click(incrementButton);
    fireEvent.click(incrementButton);
    expect(screen.getByText('Counter: 7')).toBeInTheDocument();
    
    // Then reset
    fireEvent.click(resetButton);
    expect(screen.getByText('Counter: 5')).toBeInTheDocument();
  });

  it('uses the provided step value for incrementing and decrementing', () => {
    render(<Counter step={5} />);
    const incrementButton = screen.getByRole('button', { name: /increment/i });
    const decrementButton = screen.getByRole('button', { name: /decrement/i });
    
    fireEvent.click(incrementButton);
    expect(screen.getByText('Counter: 5')).toBeInTheDocument();
    
    fireEvent.click(decrementButton);
    fireEvent.click(decrementButton);
    expect(screen.getByText('Counter: -5')).toBeInTheDocument();
  });
});