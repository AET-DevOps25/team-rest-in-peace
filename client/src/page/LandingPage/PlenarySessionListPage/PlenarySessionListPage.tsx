import usePlenaryProtocolsStore from "@/store/plenaryProtocolStore";
import { useEffect } from "react";
import PlenarySessionCard from "./components/PlenarySessionCard";
import { useInfiniteScroll } from "@/hooks/useInfiniteScroll";
import { useNavigate } from "react-router";
import type { PlenaryProtocolDto } from "@/types/PlenaryProtocolDto";

const PlenarySessionListPage = () => {
  const { protocols, loading, error, fetchProtocols, page, totalPages } =
    usePlenaryProtocolsStore();

  useEffect(() => {
    fetchProtocols();
  }, [fetchProtocols]);

  const observerRef = useInfiniteScroll({
    loading,
    hasMore: page + 1 < totalPages,
    onLoadMore: () => fetchProtocols(page + 1, 10, true),
  });

  const navigate = useNavigate();

  const handleClick = (protocol: PlenaryProtocolDto) => {
    navigate(`/protokolle/${protocol.id}`);
  };

  if (error) return <div>Error: {error}</div>;

  return (
    <div className="flex flex-col gap-6 w-full">
      {protocols.map((p) => (
        <PlenarySessionCard
          key={p.id}
          protocol={p}
          onClick={() => handleClick(p)}
        />
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

export default PlenarySessionListPage;
