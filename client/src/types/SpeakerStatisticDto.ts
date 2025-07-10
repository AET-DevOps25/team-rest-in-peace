export interface SpeakerStatisticDto {
  personId: number;
  firstName: string;
  lastName: string;
  party: string;
  lastSpeechDate: string | null;
  speechCount: number;
  totalWords: number;
}
