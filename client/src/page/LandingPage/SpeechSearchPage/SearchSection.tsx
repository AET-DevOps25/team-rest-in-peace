import { useState, useEffect } from "react";
import { useSearchParams } from "react-router";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Search, X } from "lucide-react";
import { getPartyColor, Party } from "@/global";
import useSpeechStore from "@/store/speechStore";
import useSpeakerStatisticsStore from "@/store/speakerStatisticStore";
import type { SpeakerStatisticDto } from "@/types/SpeakerStatisticDto";
import {
  MultiSelectCombobox,
  type ComboboxItem,
} from "@/components/ui/multi-select-combobox";

interface SearchSectionProps {
  selectedParties: string[];
  setSelectedParties: (parties: string[]) => void;
  selectedSpeakers: number[];
  setSelectedSpeakers: (speakers: number[]) => void;
  searchText: string;
  setSearchText: (text: string) => void;
}

const SearchSection = ({
  selectedParties,
  setSelectedParties,
  selectedSpeakers,
  setSelectedSpeakers,
  searchText,
  setSearchText,
}: SearchSectionProps) => {
  const [searchParams, setSearchParams] = useSearchParams();
  // State for politicians list
  const [politicians, setPoliticians] = useState<SpeakerStatisticDto[]>([]);
  const [politicianItems, setPoliticianItems] = useState<ComboboxItem[]>([]);

  // State local search text
  const [searchTextLocal, setSearchTextLocal] = useState<string>(
    searchText || ""
  );

  const loading = useSpeechStore((state) => state.loading);
  const {
    speakers,
    loading: speakersLoading,
    fetchSpeakers,
  } = useSpeakerStatisticsStore();

  useEffect(() => {
    fetchSpeakers(0, 100);
  }, [fetchSpeakers]);

  // Update politicians list when speakers are loaded
  useEffect(() => {
    if (speakers.length > 0) {
      setPoliticians(speakers);

      // Convert speakers to ComboboxItem format
      const items: ComboboxItem[] = speakers.map((speaker) => ({
        value: speaker.personId.toString(),
        label: `${speaker.firstName} ${speaker.lastName}`,
        color: getPartyColor(speaker.party) + " text-white",
      }));

      setPoliticianItems(items);
    }
  }, [speakers]);

  // Filter politicians based on selected parties
  useEffect(() => {
    if (selectedParties.length > 0 && politicians.length > 0) {
      // Filter politicians by selected parties and convert to ComboboxItem format
      const filteredItems: ComboboxItem[] = politicians
        .filter((p) => selectedParties.includes(p.party))
        .map((speaker) => ({
          value: speaker.personId.toString(),
          label: `${speaker.firstName} ${speaker.lastName}`,
          color: getPartyColor(speaker.party) + " text-white",
        }));

      setPoliticianItems(filteredItems);
    } else if (politicians.length > 0) {
      // If no party filter, show all politicians
      const allItems: ComboboxItem[] = politicians.map((speaker) => ({
        value: speaker.personId.toString(),
        label: `${speaker.firstName} ${speaker.lastName}`,
        color: getPartyColor(speaker.party) + " text-white",
      }));

      setPoliticianItems(allItems);
    }
  }, [selectedParties, politicians]);

  const handleSearch = () => {
    if (loading) return;

    const newParams = new URLSearchParams(searchParams);

    if (searchTextLocal) {
      newParams.set("searchText", searchTextLocal);
    } else {
      newParams.delete("searchText");
    }

    // Clear existing speakerIds
    newParams.delete("speakerIds");

    // Add selected speakers to params
    selectedSpeakers.forEach((id) => {
      newParams.append("speakerIds", id.toString());
    });

    setSearchParams(newParams, { replace: true });
    setSearchText(searchTextLocal);
  };

  const toggleParty = (party: string) => {
    if (loading) return;

    const newParams = new URLSearchParams(searchParams);
    newParams.delete("party");

    let newSelectedParties: string[];

    if (selectedParties.includes(party)) {
      // Remove party if already selected
      newSelectedParties = selectedParties.filter((p) => p !== party);
    } else {
      // Add party if not selected
      newSelectedParties = [...selectedParties, party];
    }

    // Add all selected parties to URL params
    newSelectedParties.forEach((p) => {
      newParams.append("party", p);
    });

    // Adapt selected speakers: only keep those in selected parties
    let newSelectedSpeakers = selectedSpeakers;
    if (newSelectedParties.length > 0) {
      const allowedIds = politicians
        .filter((p) => newSelectedParties.includes(p.party))
        .map((p) => p.personId);
      newSelectedSpeakers = selectedSpeakers.filter((id) =>
        allowedIds.includes(id)
      );
      // Update speakerIds in URL params
      newParams.delete("speakerIds");
      newSelectedSpeakers.forEach((id) => {
        newParams.append("speakerIds", id.toString());
      });
    }

    setSelectedParties(newSelectedParties);
    setSelectedSpeakers(newSelectedSpeakers);
    setSearchParams(newParams, { replace: true });
  };

  return (
    <Card className="gap-2">
      <CardHeader>
        <CardTitle className="text-2xl">Suche</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col gap-4">
          <div className="flex gap-2">
            <Input
              placeholder="Natürliche Sprache Suche..."
              value={searchTextLocal}
              onChange={(e) => setSearchTextLocal(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleSearch();
                }
              }}
              className="w-full flex-grow"
            />
            <Button
              onClick={handleSearch}
              disabled={loading}
              className="flex-shrink-0 flex flex-row items-center justify-center gap-2"
            >
              <Search className="w-4 h-4" />
              Suchen
            </Button>
          </div>

          <div>
            <h3 className="text-sm font-medium mb-2">Partei Filter</h3>
            <div className="flex flex-row gap-2 justify-between items-center">
              <div className="flex flex-wrap gap-2">
                {Object.values(Party).map((party) => (
                  <Badge
                    key={party}
                    variant={
                      selectedParties.includes(party) ? "default" : "outline"
                    }
                    className={`cursor-pointer ${
                      selectedParties.includes(party)
                        ? getPartyColor(party) + " text-white"
                        : ""
                    } ${loading ? "opacity-50 pointer-events-none" : ""}`}
                    onClick={() => toggleParty(party)}
                  >
                    <div className="text-sm">{party}</div>
                  </Badge>
                ))}
              </div>
              {selectedParties.length > 0 && (
                <Badge
                  variant={"outline"}
                  className={`cursor-pointer bg-gray-50 ${
                    loading ? "opacity-50 pointer-events-none" : ""
                  }`}
                  onClick={() => {
                    setSelectedParties([]);
                    const newParams = new URLSearchParams(searchParams);
                    newParams.delete("party");
                    setSearchParams(newParams, { replace: true });
                  }}
                >
                  <X />
                  <div className="text-sm">Zurücksetzen</div>
                </Badge>
              )}
            </div>
          </div>

          <div>
            <h3 className="text-sm font-medium mb-2">Politiker Filter</h3>
            {speakersLoading ? (
              <div className="text-sm text-muted-foreground p-2 border rounded-md">
                Lade Politiker...
              </div>
            ) : (
              <MultiSelectCombobox
                items={politicianItems}
                placeholder={
                  politicianItems.length === 0
                    ? "Keine Politiker gefunden"
                    : "Politiker auswählen..."
                }
                searchPlaceholder="Politiker suchen..."
                emptyMessage="Keine Politiker gefunden"
                selectedValues={selectedSpeakers.map((id) => id.toString())}
                onValueChange={(values) => {
                  const newSelectedSpeakers = values.map((v) =>
                    parseInt(v, 10)
                  );
                  setSelectedSpeakers(newSelectedSpeakers);

                  // Update URL params
                  const newParams = new URLSearchParams(searchParams);
                  newParams.delete("speakerIds");

                  newSelectedSpeakers.forEach((id) => {
                    newParams.append("speakerIds", id.toString());
                  });

                  setSearchParams(newParams, { replace: true });
                }}
                disabled={loading || politicianItems.length <= 0}
                deleteAll={() => {
                  setSelectedSpeakers([]);
                  const newParams = new URLSearchParams(searchParams);
                  newParams.delete("speakerIds");
                  setSearchParams(newParams, { replace: true });
                }}
              />
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default SearchSection;
