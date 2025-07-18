import { useInfiniteScroll } from "@/hooks/useInfiniteScroll";
import useSpeechStore from "@/store/speechStore";
import { useEffect, useState } from "react";
import SpeechCard from "../SpeechListPage/components/SpeechCard";
import SearchSection from "./SearchSection";
import { useSearchParams } from "react-router";

const SpeechSearchPage = () => {
  const {
    speeches,
    loading,
    error,
    fetchSpeeches,
    page,
    totalPages,
  } = useSpeechStore();

  const [searchParams] = useSearchParams();
  const parties = searchParams.getAll("parties");
  const searchText = searchParams.get("searchText");
  const speakerIdsParam = searchParams.getAll("speakerIds");
  const speakerIds = speakerIdsParam.map(id => parseInt(id, 10));

  const [title, setTitle] = useState("Erweiterte Suche");

  // Function to handle search from SearchSection
  const handleSearch = (searchText: string, parties?: string[], speakerIds?: number[]) => {
    fetchSpeeches(0, 10, false, {
      parties: parties,
      searchText: searchText,
      speakerIds: speakerIds,
    });
  };

  // Update title when searchText changes, but don't fetch speeches automatically
  useEffect(() => {
    if (searchText) {
      setTitle(`Suchergebnisse für "${searchText}"`);
    } else {
      setTitle("Erweiterte Suche");
    }
  }, [searchText]);

  // Handle direct URL navigation with search params
  useEffect(() => {
    // Only run this effect once when the component mounts
    if (searchText && speeches.length === 0) {
      // This is likely a direct URL navigation with search params, not a tab change
      handleSearch(
        searchText, 
        parties || undefined,
        speakerIds.length > 0 ? speakerIds : undefined
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // Empty dependency array means this runs once on mount

  const observerRef = useInfiniteScroll({
    loading,
    hasMore: page + 1 < totalPages,
    onLoadMore: () =>
      fetchSpeeches(page + 1, 10, true, {
        parties: parties || undefined,
        searchText: searchText || undefined,
        speakerIds: speakerIds.length > 0 ? speakerIds : undefined,
      }),
  });

  if (error) return <div>Error: {error}</div>;

  return (
    <div className="flex flex-col gap-6 w-full">
      <SearchSection onSearch={handleSearch} />
      {speeches.length > 0 ? (
        <>
          {searchText && (
            <h2 className="text-xl font-semibold">{title}</h2>
          )}
          {speeches.map((s, i) => (
            <SpeechCard key={`${s.firstName}-${s.lastName}-${i}`} speech={s} />
          ))}
        </>
      ) : (
        <div className="text-center py-8">
          {searchText ? (
            <p>Keine Ergebnisse gefunden. Versuchen Sie eine andere Suchanfrage.</p>
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
