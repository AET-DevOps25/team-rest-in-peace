import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { formatWords, getPartyColor } from "@/global";
import type { SpeechDto } from "@/types/SpeechDto";
import { ChevronDown, ChevronUp } from "lucide-react";
import { useState } from "react";

const SpeechCard = ({ speech }: { speech: SpeechDto }) => {
  const [open, setOpen] = useState(false);

  return (
    <Card className="hover:shadow-md transition-shadow cursor-pointer">
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-2">
              <Badge className={`${getPartyColor(speech.party)} text-white`}>
                {speech.party ? speech.party : "Unbekannt"}
              </Badge>
              <span className="text-sm text-muted-foreground">
                {new Date(
                  speech.protocolDate ? speech.protocolDate : ""
                ).toLocaleDateString("de-DE")}
              </span>
              <span className="text-sm text-muted-foreground">
                {formatWords(speech.wordCount)}
              </span>
            </div>
            <CardTitle className="text-lg">
              {speech.firstName} {speech.lastName}
            </CardTitle>
            <CardDescription className="text-base font-medium mb-2">
              {speech.agendaItemTitle}
            </CardDescription>
            <p className="text-sm text-muted-foreground">
              {speech.protocolName}
            </p>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          <p className="text-muted-foreground leading-relaxed">
            {speech.textSummary
              ? speech.textSummary
              : "Keine Zusammenfassung verfügbar"}
          </p>

          {open && (
            <div className="border-t pt-4">
              <h4 className="font-semibold mb-2">Vollständige Rede:</h4>
              <p className="leading-relaxed whitespace-pre-line">
                {speech.textPlain}
              </p>
            </div>
          )}

          <Button
            variant="outline"
            onClick={() => setOpen(!open)}
            className="w-full"
          >
            {open ? (
              <>
                <ChevronUp className="w-4 h-4 mr-2" />
                Weniger anzeigen
              </>
            ) : (
              <>
                <ChevronDown className="w-4 h-4 mr-2" />
                Vollständige Rede anzeigen
              </>
            )}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};

export default SpeechCard;
