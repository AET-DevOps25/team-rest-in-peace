import { useInfiniteScroll } from "@/hooks/useInfiniteScroll";
import useSpeakerStatisticsStore from "@/store/speakerStatisticStore";
import { useEffect } from "react";
import SpeakerStatisticCard from "./components/SpeakerStatisticCard";
import { useNavigate } from "react-router";
import type { SpeakerStatisticDto } from "@/types/SpeakerStatisticDto";

const SpeakerStatisticsListPage = () => {
  const { speakers, loading, error, fetchSpeakers, page, totalPages } =
    useSpeakerStatisticsStore();

  useEffect(() => {
    fetchSpeakers();
  }, [fetchSpeakers]);

  const observerRef = useInfiniteScroll({
    loading,
    hasMore: page + 1 < totalPages,
    onLoadMore: () => fetchSpeakers(page + 1, 12, true),
  });

  const navigate = useNavigate();

  const handleClick = (speaker: SpeakerStatisticDto) => {
    navigate(`/redner/${speaker.personId}`);
  };

  if (error) return <div>Error: {error}</div>;

  return (
    <div className="flex flex-col gap-6 w-full">
      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
        {speakers.map((s) => (
          <SpeakerStatisticCard
            key={s.personId}
            speaker={s}
            onClick={() => handleClick(s)}
          />
        ))}
      </div>
      {loading && (
        <div className="flex flex-col items-center justify-center w-full py-4">
          <p>Loading...</p>
        </div>
      )}
      <div ref={observerRef} />
    </div>
  );
};

export default SpeakerStatisticsListPage;
