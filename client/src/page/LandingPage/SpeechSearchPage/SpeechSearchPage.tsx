import { useInfiniteScroll } from "@/hooks/useInfiniteScroll";
import useSpeechStore from "@/store/speechStore";
import { useEffect, useState } from "react";
import SpeechCard from "../SpeechListPage/components/SpeechCard";
import SearchSection from "./SearchSection";
import { useSearchParams } from "react-router";

const SpeechSearchPage = () => {
  const { speeches, loading, error, fetchSpeeches, page, totalPages } =
    useSpeechStore();

  const [searchParams] = useSearchParams();
  const [parties, setParties] = useState<string[]>(
    searchParams.getAll("party")
  );
  const [speakerIds, setSpeakerIds] = useState<number[]>(
    searchParams.getAll("speakerIds").map((id) => parseInt(id, 10))
  );
  const [searchText, setSearchText] = useState<string>(
    searchParams.get("searchText") || ""
  );

  const [title, setTitle] = useState("Erweiterte Suche");

  // Update title when searchText changes, but don't fetch speeches automatically
  useEffect(() => {
    if (searchText) {
      setTitle(`Suchergebnisse für "${searchText}"`);
    } else {
      setTitle("Erweiterte Suche");
    }
  }, [searchText]);

  const observerRef = useInfiniteScroll({
    loading,
    hasMore: page + 1 < totalPages,
    onLoadMore: () =>
      fetchSpeeches(page + 1, 10, true, {
        parties: parties || [],
        searchText: searchText || "",
        speakerIds: speakerIds.length > 0 ? speakerIds : [],
      }),
  });

  useEffect(() => {
    fetchSpeeches(0, 10, false, {
      parties: parties || [],
      searchText: searchText || "",
      speakerIds: speakerIds.length > 0 ? speakerIds : [],
    });
  }, [parties, speakerIds, searchText, fetchSpeeches]);

  if (error) return <div>Error: {error}</div>;

  return (
    <div className="flex flex-col gap-6 w-full">
      <SearchSection
        selectedParties={parties}
        setSelectedParties={setParties}
        selectedSpeakers={speakerIds}
        setSelectedSpeakers={setSpeakerIds}
        searchText={searchText}
        setSearchText={setSearchText}
      />
      {speeches.length > 0 ? (
        <>
          {searchText && <h2 className="text-xl font-semibold">{title}</h2>}
          {speeches.map((s, i) => (
            <SpeechCard key={`${s.firstName}-${s.lastName}-${i}`} speech={s} />
          ))}
        </>
      ) : (
        <div className="text-center py-8">
          {searchText ? (
            <p>
              Keine Ergebnisse gefunden. Versuchen Sie eine andere Suchanfrage.
            </p>
          ) : (
            <p>Geben Sie einen Suchbegriff ein, um Reden zu finden.</p>
          )}
        </div>
      )}
      {loading && (
        <div className="flex flex-col items-center justify-center w-full py-4">
          <p>Loading...</p>
        </div>
      )}
      <div ref={observerRef} />
    </div>
  );
};

export default SpeechSearchPage;
