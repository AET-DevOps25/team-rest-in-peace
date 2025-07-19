import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { formatWords, getPartyColor } from "@/global";
import type { PartyStatisticDto } from "@/types/PartyStatisticDto";
import { useState } from "react";
import NotificationModal from "../../components/NotificationModal";
import { Button } from "@/components/ui/button";
import { Bell } from "lucide-react";

interface PartyStatisticCardProps {
  party: PartyStatisticDto;
  onClick?: () => void;
}

const PartyStatisticCard = ({ party, onClick }: PartyStatisticCardProps) => {
  const [showNotificationModal, setShowNotificationModal] = useState(false);

  return (
    <Card
      key={party.party}
      onClick={() => {
        if (!showNotificationModal && onClick) {
          onClick();
        }
      }}
      className="hover:shadow-md transition-shadow cursor-pointer justify-between"
    >
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <CardTitle className="text-lg">
              <div className="flex justify-between items-center gap-2">
                <div className="flex items-center gap-2">
                  <Badge
                    className={`${getPartyColor(
                      party.party
                    )} text-white text-sm`}
                  >
                    {party.party}
                  </Badge>
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size={"sm"}
                    onClick={(e) => {
                      e.stopPropagation();
                      setShowNotificationModal(true);
                    }}
                  >
                    <Bell className="w-4 h-4" />
                  </Button>
                </div>
              </div>
            </CardTitle>
            <p className="text-sm text-muted-foreground">
              Letzte Rede am:{" "}
              {new Date(
                party.lastSpeechDate ? party.lastSpeechDate : ""
              ).toLocaleDateString("de-DE")}
            </p>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-3 gap-4 text-center">
          <div>
            <div className="text-lg font-semibold">{party.speechCount}</div>
            <div className="text-xs text-muted-foreground">Reden</div>
          </div>
          <div>
            <div className="text-lg font-semibold">
              {formatWords(party.totalWords)}
            </div>
            <div className="text-xs text-muted-foreground">Gesamt</div>
          </div>
          <div>
            <div className="text-lg font-semibold">{party.personCount}</div>
            <div className="text-xs text-muted-foreground">Redner</div>
          </div>
        </div>
      </CardContent>
      <NotificationModal
        isOpen={showNotificationModal}
        onClose={() => setShowNotificationModal(false)}
        type="PARTY"
        party={party.party}
      />
    </Card>
  );
};

export default PartyStatisticCard;
