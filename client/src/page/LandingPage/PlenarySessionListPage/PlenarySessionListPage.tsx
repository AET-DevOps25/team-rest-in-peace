import usePlenaryProtocolsStore from "@/store/plenaryProtocolStore";
import { useCallback, useEffect, useRef } from "react";
import PlenarySessionCard from "./components/PlenarySessionCard";
import { Skeleton } from "@/components/ui/skeleton";

const PlenarySessionListPage = () => {
  const { protocols, loading, error, fetchProtocols, page, totalPages } =
    usePlenaryProtocolsStore();
  const observerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    fetchProtocols();
  }, [fetchProtocols]);

  const handleObserver = useCallback(
    (entries: IntersectionObserverEntry[]) => {
      const target = entries[0];
      if (target.isIntersecting && page + 1 < totalPages && !loading) {
        fetchProtocols(page + 1, 10, true); // load next page and append
      }
    },
    [fetchProtocols, page, totalPages, loading]
  );

  useEffect(() => {
    const option = {
      root: null,
      rootMargin: "20px",
      threshold: 1.0,
    };
    const observer = new IntersectionObserver(handleObserver, option);
    const currentRef = observerRef.current;
    if (currentRef) observer.observe(currentRef);

    return () => {
      if (currentRef) observer.unobserve(currentRef);
    };
  }, [handleObserver]);

  if (error) return <div>Error: {error}</div>;

  return (
    <div className="flex flex-col gap-6 w-full">
      {protocols.map((p) => (
        <PlenarySessionCard key={p.id} protocol={p} />
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
