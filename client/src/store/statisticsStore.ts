import { BROWSING_BASE_URL } from "@/global";
import type { StatisticsDto } from "@/types/StatisticsDto";
import { create } from "zustand";

interface StatisticsStoreState {
  statistics: StatisticsDto;
  setStatistics: () => Promise<void>;
}

const api = {
  getStatistics: async (): Promise<StatisticsDto> => {
    const res = await fetch(`${BROWSING_BASE_URL}/statistics`);

    if (!res.ok) {
      throw new Error(
        `Failed to fetch statistics: ${res.status} ${res.statusText}`
      );
    }

    return res.json();
  },
};

const useStatisticsStore = create<StatisticsStoreState>((set) => ({
  statistics: {
    plenaryCount: 0,
    speakerCount: 0,
    wordCount: 0,
    partyCount: 0,
  },
  setStatistics: async () => {
    const data = await api.getStatistics();
    set(() => ({ statistics: data }));
  },
}));

export default useStatisticsStore;
