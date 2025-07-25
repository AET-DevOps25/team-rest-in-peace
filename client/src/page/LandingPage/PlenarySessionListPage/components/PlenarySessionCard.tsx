import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { formatWords, getPartyColor } from "@/global";
import type { PlenaryProtocolDto } from "@/types/PlenaryProtocolDto";
import { Calendar, FileText, Users } from "lucide-react";

interface PlenarySessionCardProps {
  protocol: PlenaryProtocolDto;
  onClick?: () => void;
}

const PlenarySessionCard = ({ protocol, onClick }: PlenarySessionCardProps) => {
  return (
    <Card className="hover:shadow-lg cursor-pointer" onClick={onClick}>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <CardTitle className="text-xl mb-2">{protocol.title}</CardTitle>
            <div className="flex items-center gap-4 text-sm text-muted-foreground mb-3">
              <div className="flex items-center gap-1">
                <Calendar className="w-4 h-4" />
                {protocol.date
                  ? new Date(protocol.date).toLocaleDateString("de-DE", {
                      day: "2-digit",
                      month: "2-digit",
                      year: "numeric",
                    })
                  : "Kein Datum"}
              </div>
              <div className="flex items-center gap-1">
                <Users className="w-4 h-4" />
                {protocol.speakerCount} Redner
              </div>
              <div className="flex items-center gap-1">
                <FileText className="w-4 h-4" />
                {formatWords(protocol.totalWords)}
              </div>
            </div>
            <CardDescription className="text-base leading-relaxed">
              {protocol.summary ? (
                <>{protocol.summary}</>
              ) : (
                <>Keine Zusammenfassung verfügbar</>
              )}
            </CardDescription>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          <h4 className="font-semibold text-sm">
            {protocol.partyStats.length > 0
              ? `Wortanteil nach Parteien:`
              : `Keine Daten verfügbar`}
          </h4>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
            {protocol.partyStats
              .sort((a, b) => b.percentage - a.percentage)
              .map((stat) => (
                <div key={stat.party} className="text-center">
                  <Badge
                    variant="outline"
                    className="w-full justify-center mb-1"
                  >
                    {stat.party ? stat.party : "Unbekannt"}
                  </Badge>
                  <div className="text-sm font-medium">
                    {stat.percentage.toFixed(1)}%
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                    <div
                      className={`h-2 rounded-full ${getPartyColor(
                        stat.party
                      )}`}
                      style={{
                        width: `${stat.percentage}%`,
                      }}
                    />
                  </div>
                </div>
              ))}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default PlenarySessionCard;
