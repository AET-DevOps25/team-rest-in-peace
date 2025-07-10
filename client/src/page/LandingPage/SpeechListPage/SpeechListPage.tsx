import { useInfiniteScroll } from "@/hooks/useInfiniteScroll";
import useSpeechStore from "@/store/speechStore";
import { useEffect } from "react";
import SpeechCard from "./components/SpeechCard";

const SpeechListPage = () => {
  const { speeches, loading, error, fetchSpeeches, page, totalPages } =
    useSpeechStore();

  useEffect(() => {
    fetchSpeeches();
  }, [fetchSpeeches]);

  const observerRef = useInfiniteScroll({
    loading,
    hasMore: page + 1 < totalPages,
    onLoadMore: () => fetchSpeeches(page + 1, 10, true),
  });

  if (error) return <div>Error: {error}</div>;

  return (
    <div className="flex flex-col gap-6 w-full">
      {speeches.map((s, i) => (
        <SpeechCard key={`${s.firstName}-${s.lastName}-${i}`} speech={s} />
      ))}
      {loading && (
        <div className="flex flex-col items-center justify-center w-full py-4">
          <p>Loading...</p>
        </div>
      )}
      <div ref={observerRef} />
    </div>
  );
};

export default SpeechListPage;
