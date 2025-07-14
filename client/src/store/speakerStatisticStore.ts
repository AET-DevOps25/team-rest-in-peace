import { BROWSING_BASE_URL } from "@/global";
import type { SpeakerStatisticDto } from "@/types/SpeakerStatisticDto";
import { create } from "zustand";

const api = {
  getSpeakerStatistics: async (
    page = 0,
    size = 12
  ): Promise<{
    content: SpeakerStatisticDto[];
    totalPages: number;
    totalElements: number;
  }> => {
    const res = await fetch(
      `${BROWSING_BASE_URL}/speakers?page=${page}&size=${size}`
    );

    if (!res.ok) {
      throw new Error(
        `Failed to fetch speaker statistics: ${res.status} ${res.statusText}`
      );
    }

    return res.json();
  },
};

interface SpeakerStatisticState {
  speakers: SpeakerStatisticDto[];
  loading: boolean;
  error: string | null;
  page: number;
  size: number;
  totalPages: number;
  fetchSpeakers: (
    page?: number,
    size?: number,
    append?: boolean
  ) => Promise<void>;
}

const useSpeakerStatisticsStore = create<SpeakerStatisticState>((set) => ({
  speakers: [],
  loading: false,
  error: null,
  page: 0,
  size: 12,
  totalPages: 0,

  fetchSpeakers: async (page = 0, size = 12, append = false) => {
    set({ loading: true, error: null });
    try {
      const data = await api.getSpeakerStatistics(page, size);
      set((state) => ({
        speakers: append ? [...state.speakers, ...data.content] : data.content,
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

export default useSpeakerStatisticsStore;
