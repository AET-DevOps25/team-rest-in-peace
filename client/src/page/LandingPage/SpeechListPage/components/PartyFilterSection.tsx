import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getPartyColor, Party } from "@/global";
import useSpeechStore from "@/store/speechStore";
import { Bell } from "lucide-react";
import { useState } from "react";
import { useSearchParams } from "react-router";
import NotificationModal from "../../components/NotificationModal";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";

const PartyFilterSection = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const selectedParty = searchParams.get("party");

  const loading = useSpeechStore((state) => state.loading);

  const toggleParty = (party: string) => {
    if (loading) return;

    const newParams = new URLSearchParams(searchParams);

    if (party === selectedParty) {
      newParams.delete("party");
    } else {
      newParams.set("party", party);
    }

    setSearchParams(newParams, { replace: true });
  };

  const [showNotificationModal, setShowNotificationModal] = useState(false);

  return (
    <Card>
      <CardHeader>
        <div className="flex flex-row items-center justify-between">
          <CardTitle className="mb-2 text-2xl">Partei Filter</CardTitle>
          <Tooltip>
            <TooltipTrigger asChild>
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size={"sm"}
                  onClick={() => {
                    setShowNotificationModal(true);
                  }}
                  disabled={!selectedParty}
                >
                  <Bell className="w-4 h-4" />
                </Button>
              </div>
            </TooltipTrigger>
            <TooltipContent>
              <p>
                {selectedParty
                  ? "Benachrichtigungen für neue Reden dieser Partei aktivieren"
                  : "Bitte wählen Sie zuerst eine Partei aus"}
              </p>
            </TooltipContent>
          </Tooltip>
        </div>
        <CardContent>
          <div className="flex flex-wrap gap-2">
            {Object.values(Party).map((party) => (
              <Badge
                key={party}
                variant={selectedParty === party ? "default" : "outline"}
                className={`cursor-pointer ${
                  selectedParty === party
                    ? getPartyColor(party) + " text-white"
                    : ""
                } ${loading ? "opacity-50 pointer-events-none" : ""}`} // visually and functionally disabled
                onClick={() => toggleParty(party)}
              >
                <div className="text-sm">{party}</div>
              </Badge>
            ))}
          </div>
        </CardContent>
      </CardHeader>

      <NotificationModal
        isOpen={showNotificationModal}
        onClose={() => setShowNotificationModal(false)}
        type="PARTY"
        party={selectedParty || ""}
      />
    </Card>
  );
};

export default PartyFilterSection;
