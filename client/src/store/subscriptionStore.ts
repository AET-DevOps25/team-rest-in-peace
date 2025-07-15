import { NOTIFICATIONS_BASE_URL } from "@/global";
import { create } from "zustand";

interface SubscriptionStoreState {
  loading: boolean;
  error: string | null;
  success: boolean;
  unsubscribe: (email: string) => Promise<void>;
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
}));

export default useSubscriptionStore;
