import { BROWSING_BASE_URL } from "@/global";
import type { SpeechDto } from "@/types/SpeechDto";
import { create } from "zustand";

const api = {
  getSpeeches: async (
    page = 0,
    size = 10,
    filters?: {
      parties?: string[];
      speakerIds?: number[];
      plenaryProtocolId?: number;
      searchText?: string;
      searchSimilarityThreshold?: number;
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

    if (filters?.parties) {
      params.set("parties", filters.parties ? filters.parties.join(",") : "");
    }

    if (filters?.speakerIds && filters.speakerIds.length > 0) {
      params.set(
        "speakerIds",
        filters.speakerIds ? filters.speakerIds.join(",") : ""
      );
    }

    if (filters?.searchText) {
      params.append("searchText", filters.searchText);
    }

    if (filters?.searchSimilarityThreshold) {
      params.append(
        "searchSimilarityThreshold",
        filters.searchSimilarityThreshold.toString()
      );
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
  getPlenaryProtocolName: async (id: number): Promise<string> => {
    const res = await fetch(
      `${BROWSING_BASE_URL}/plenary-protocols/${id}/name`
    );

    if (!res.ok) {
      throw new Error(
        `Failed to fetch plenary protocol name: ${res.status} ${res.statusText}`
      );
    }

    return res.text(); // because it's a plain string response
  },
  getSpeakerName: async (id: number): Promise<string> => {
    const res = await fetch(`${BROWSING_BASE_URL}/speaker/${id}/name`);

    if (!res.ok) {
      throw new Error(
        `Failed to fetch speaker name: ${res.status} ${res.statusText}`
      );
    }

    return res.text();
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
      parties?: string[];
      speakerIds?: number[];
      plenaryProtocolId?: number;
      searchText?: string;
      searchSimilarityThreshold?: number;
    }
  ) => Promise<void>;
  getPlenaryProtocolName: (id: number) => Promise<string>;
  getSpeakerName: (id: number) => Promise<string>;
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
  getPlenaryProtocolName: async (id: number) => {
    try {
      return await api.getPlenaryProtocolName(id);
    } catch (error: unknown) {
      throw new Error(error instanceof Error ? error.message : String(error));
    }
  },
  getSpeakerName: async (id: number) => {
    try {
      return await api.getSpeakerName(id);
    } catch (error: unknown) {
      throw new Error(error instanceof Error ? error.message : String(error));
    }
  },
}));

export default useSpeechStore;
