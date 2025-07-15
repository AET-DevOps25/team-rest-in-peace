import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { formatWords, getPartyColor } from "@/global";
import type { SpeakerStatisticDto } from "@/types/SpeakerStatisticDto";
import { useState } from "react";
import NotificationModal from "../../components/NotificationModal";
import { Button } from "@/components/ui/button";
import { Bell } from "lucide-react";

interface SpeakerStatisticCardProps {
  speaker: SpeakerStatisticDto;
  onClick?: () => void;
}

const SpeakerStatisticCard = ({
  speaker,
  onClick,
}: SpeakerStatisticCardProps) => {
  const [showNotificationModal, setShowNotificationModal] = useState(false);

  return (
    <Card
      key={speaker.personId}
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
              <div className="flex justify-between items-center">
                <div className="flex flex-row items-center gap-2">
                  {speaker.firstName} {speaker.lastName}
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
                <div className="flex items-center gap-2">
                  <Badge
                    className={`${getPartyColor(
                      speaker.party
                    )} text-white text-xs`}
                  >
                    {speaker.party ? speaker.party : "Unbekannt"}
                  </Badge>
                </div>
              </div>
            </CardTitle>
            <p className="text-sm text-muted-foreground">
              Letzte Rede am:{" "}
              {new Date(
                speaker.lastSpeechDate ? speaker.lastSpeechDate : ""
              ).toLocaleDateString("de-DE")}
            </p>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-2 gap-4 text-center">
          <div>
            <div className="text-lg font-semibold">{speaker.speechCount}</div>
            <div className="text-xs text-muted-foreground">Reden</div>
          </div>
          <div>
            <div className="text-lg font-semibold">
              {formatWords(speaker.totalWords)}
            </div>
            <div className="text-xs text-muted-foreground">Gesamt</div>
          </div>
        </div>
      </CardContent>
      <NotificationModal
        isOpen={showNotificationModal}
        onClose={() => setShowNotificationModal(false)}
        type="speaker"
        speaker={{
          name: `${speaker.firstName} ${speaker.lastName}`,
          id: speaker.personId,
          party: speaker.party,
        }}
      />
    </Card>
  );
};

export default SpeakerStatisticCard;
