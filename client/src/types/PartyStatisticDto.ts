export interface PartyStatisticDto {
  party: string;
  lastSpeechDate: string | null;
  speechCount: number;
  totalWords: number;
  personCount: number;
}