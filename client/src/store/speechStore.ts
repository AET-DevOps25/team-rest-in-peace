import { BROWSING_BASE_URL } from "@/global";
import type { SpeechDto } from "@/types/SpeechDto";
import { create } from "zustand";

const api = {
  getSpeeches: async (
    page = 0,
    size = 10,
    filters?: {
      party?: string;
      speakerId?: number;
      plenaryProtocolId?: number;
    }
  ): Promise<{
    content: SpeechDto[];
    totalPages: number;
    totalElements: number;
  }> => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (filters?.party) {
      params.append("party", filters.party);
    }
    if (filters?.speakerId !== undefined) {
      params.append("speakerId", filters.speakerId.toString());
    }
    if (filters?.plenaryProtocolId !== undefined) {
      params.append("plenaryProtocolId", filters.plenaryProtocolId.toString());
    }

    const res = await fetch(
      `${BROWSING_BASE_URL}/speeches?${params.toString()}`
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
    append?: boolean,
    filters?: {
      party?: string;
      speakerId?: number;
      plenaryProtocolId?: number;
    }
  ) => Promise<void>;
}

const useSpeechStore = create<SpeechStoreState>((set) => ({
  speeches: [],
  loading: false,
  error: null,
  page: 0,
  size: 10,
  totalPages: 0,

  fetchSpeeches: async (page = 0, size = 10, append = false, filters) => {
    set({ loading: true, error: null });
    try {
      const data = await api.getSpeeches(page, size, filters);
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
