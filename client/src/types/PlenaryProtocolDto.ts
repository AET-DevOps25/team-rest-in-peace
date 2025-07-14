import type { PlenaryProtocolPartyStatsDto } from "./PlenaryProtocolPartyStatsDto";

export interface PlenaryProtocolDto {
  id: string;
  date: string | null;
  title: string;
  summary: string | null;
  speakerCount: number;
  totalWords: number;
  partyStats: PlenaryProtocolPartyStatsDto[];
}
