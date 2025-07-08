import { BROWSING_BASE_URL } from "@/global";
import type { PlenaryProtocolDto } from "@/types/PlenaryProtocolDto";
import { create } from "zustand";

interface PlenaryProtocolsState {
  protocols: PlenaryProtocolDto[];
  loading: boolean;
  error: string | null;
  page: number;
  size: number;
  totalPages: number;
  fetchProtocols: (
    page?: number,
    size?: number,
    append?: boolean
  ) => Promise<void>;
}

const api = {
  getPlenaryProtocols: async (
    page = 0,
    size = 10
  ): Promise<{
    content: PlenaryProtocolDto[];
    totalPages: number;
    totalElements: number;
  }> => {
    const res = await fetch(
      `${BROWSING_BASE_URL}/plenary-protocols?page=${page}&size=${size}`
    );

    if (!res.ok) {
      throw new Error(
        `Failed to fetch plenary protocols: ${res.status} ${res.statusText}`
      );
    }

    return res.json();
  },
};

const usePlenaryProtocolsStore = create<PlenaryProtocolsState>((set) => ({
  protocols: [],
  loading: false,
  error: null,
  page: 0,
  size: 10,
  totalPages: 0,

  fetchProtocols: async (page = 0, size = 10, append = false) => {
    set({ loading: true, error: null });

    try {
      const data = await api.getPlenaryProtocols(page, size);
      set((state) => ({
        protocols: append
          ? [...state.protocols, ...data.content]
          : data.content,
        page,
        size,
        totalPages: data.totalPages,
        loading: false,
      }));
    } catch (error: unknown) {
      set({
        error: error instanceof Error ? error.message : String(error),
        loading: false,
      });
    }
  },
}));

export default usePlenaryProtocolsStore;
