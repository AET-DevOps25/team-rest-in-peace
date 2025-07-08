import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import type { PlenaryProtocolDto } from "@/types/PlenaryProtocolDto";
import { Calendar, FileText, Users } from "lucide-react";

interface PlenarySessionCardProps {
  protocol: PlenaryProtocolDto;
}

const PlenarySessionCard = ({ protocol }: PlenarySessionCardProps) => {
  function formatWords(words: number) {
    if (words >= 1000) {
      return `${(words / 1000).toFixed(1)}k Wörter`;
    }
    return `${words} Wörter`;
  }

  function getPartyColor(party: string) {
    const colors = {
      SPD: "bg-red-500",
      "CDU/CSU": "bg-gray-800",
      "BÜNDNIS 90/DIE GRÜNEN": "bg-green-500",
      FDP: "bg-yellow-500",
      AfD: "bg-blue-600",
      "Die Linke": "bg-pink-500",
      BSW: "bg-purple-700",
      fraktionslos: "bg-gray-500",
    };

    return colors[party as keyof typeof colors] || "bg-fuchsia-900";
  }

  return (
    <Card className="hover:shadow-lg cursor-pointer">
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
