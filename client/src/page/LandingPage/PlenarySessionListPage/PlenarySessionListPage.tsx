import usePlenaryProtocolsStore from "@/store/plenaryProtocolStore";
import { useEffect, useState } from "react";
import PlenarySessionCard from "./components/PlenarySessionCard";
import { useInfiniteScroll } from "@/hooks/useInfiniteScroll";
import { useNavigate } from "react-router";
import type { PlenaryProtocolDto } from "@/types/PlenaryProtocolDto";
import { Button } from "@/components/ui/button";
import { Bell } from "lucide-react";
import NotificationModal from "../components/NotificationModal";

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

  const [showNotificationModal, setShowNotificationModal] = useState(false);

  if (error) return <div>Error: {error}</div>;

  return (
    <div className="flex flex-col gap-6 w-full">
      <div className="flex flex-row w-full justify-center">
        <Button
          variant="outline"
          onClick={() => setShowNotificationModal(true)}
        >
          <Bell className="w-4 h-4 mr-2" />
          Benachrichtigungen für neue Sitzungen
        </Button>
      </div>
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

      <NotificationModal
        isOpen={showNotificationModal}
        onClose={() => setShowNotificationModal(false)}
        type="PLENARY_PROTOCOL"
      />
    </div>
  );
};

export default PlenarySessionListPage;
