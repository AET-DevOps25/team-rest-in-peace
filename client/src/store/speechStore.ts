import { BROWSING_BASE_URL } from "@/global";
import type { SpeechDto } from "@/types/SpeechDto";
import { create } from "zustand";

const api = {
  // existing endpoints...

  getSpeeches: async (
    page = 0,
    size = 10
  ): Promise<{
    content: SpeechDto[];
    totalPages: number;
    totalElements: number;
  }> => {
    const res = await fetch(
      `${BROWSING_BASE_URL}/speeches?page=${page}&size=${size}`
    );

    if (!res.ok) {
      throw new Error(
        `Failed to fetch speeches: ${res.status} ${res.statusText}`
      );
    }

    return res.json();
  },
};

interface SpeechStoreState {
  speeches: SpeechDto[];
  loading: boolean;
  error: string | null;
  page: number;
  size: number;
  totalPages: number;
  fetchSpeeches: (
    page?: number,
    size?: number,
    append?: boolean
  ) => Promise<void>;
}

const useSpeechStore = create<SpeechStoreState>((set) => ({
  speeches: [],
  loading: false,
  error: null,
  page: 0,
  size: 10,
  totalPages: 0,

  fetchSpeeches: async (page = 0, size = 10, append = false) => {
    set({ loading: true, error: null });
    try {
      const data = await api.getSpeeches(page, size);
      set((state) => ({
        speeches: append ? [...state.speeches, ...data.content] : data.content,
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

export default useSpeechStore;
