import { NOTIFICATIONS_BASE_URL } from "@/global";
import { create } from "zustand";

interface SubscriptionStoreState {
  loading: boolean;
  error: string | null;
  success: boolean;
  unsubscribe: (email: string) => Promise<void>;
  subscribe: (data: {
    type: "PLENARY_PROTOCOL" | "PERSON" | "PARTY";
    email: string;
    personId?: number;
    party?: string;
  }) => Promise<void>;
  reset: () => void;
}
const api = {
  unsubscribe: async (email: string): Promise<void> => {
    const res = await fetch(
      `${NOTIFICATIONS_BASE_URL}/unsubscribe?email=${encodeURIComponent(
        email
      )}`,
      {
        method: "DELETE",
      }
    );

    if (!res.ok) {
      throw new Error(`Failed to unsubscribe: ${res.status} ${res.statusText}`);
    }
  },
  subscribe: async (data: {
    type: "PLENARY_PROTOCOL" | "PERSON" | "PARTY";
    email: string;
    personId?: number;
    party?: string;
  }): Promise<void> => {
    const res = await fetch(`${NOTIFICATIONS_BASE_URL}/subscribe`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const json = await res.json();

    if (!res.ok || !json.success) {
      throw new Error(json.error || "Subscription failed");
    }
  },
};

const useSubscriptionStore = create<SubscriptionStoreState>((set) => ({
  loading: false,
  error: null,
  success: false,
  unsubscribe: async (email: string) => {
    set({ loading: true, error: null, success: false });

    try {
      await api.unsubscribe(email);
      set({ loading: false, success: true });
    } catch (error) {
      set({ loading: false, error: (error as Error).message });
    }
  },
  subscribe: async (data) => {
    set({ loading: true, error: null, success: false });

    try {
      await api.subscribe(data);
      set({ loading: false, success: true });
    } catch (error) {
      set({ loading: false, error: (error as Error).message });
    }
  },
  reset: () => set({ loading: false, error: null, success: false }),
}));

export default useSubscriptionStore;
