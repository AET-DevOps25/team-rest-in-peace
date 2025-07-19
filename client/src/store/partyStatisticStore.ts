import {BROWSING_BASE_URL} from "@/global";
import type {PartyStatisticDto} from "@/types/PartyStatisticDto";
import {create} from "zustand";

const api = {
        getPartyStatistics: async (): Promise<PartyStatisticDto[]> => {
            const res = await fetch(
                `${BROWSING_BASE_URL}/parties`
            );

            if (!res.ok) {
                throw new Error(
                    `Failed to fetch party statistics: ${res.status} ${res.statusText}`
                );
            }
            return res.json();
        }
        ,
    }
;

interface PartyStatisticState {
    parties: PartyStatisticDto[];
    loading: boolean;
    error: string | null;
    fetchParties: () => Promise<void>;
}

const usePartyStatisticsStore = create<PartyStatisticState>((set) => ({
    parties: [],
    loading: false,
    error: null,

    fetchParties: async () => {
        set({loading: true, error: null});
        try {
            const data = await api.getPartyStatistics();
            set(() => ({
                parties: data,
                loading: false,
            }));
        } catch (error: unknown) {
            console.error(error);
            set({
                error: error instanceof Error ? error.message : String(error),
                loading: false,
            });
        }
    },
}));

export default usePartyStatisticsStore;