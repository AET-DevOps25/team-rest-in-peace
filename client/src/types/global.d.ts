export {};

declare global {
  interface Window {
    RUNTIME_ENVIRONMENT_VARIABLES?: Record<string, string | undefined>;
  }
}
