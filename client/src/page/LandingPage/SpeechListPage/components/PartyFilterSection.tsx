import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getPartyColor, Party } from "@/global";
import useSpeechStore from "@/store/speechStore";
import { useSearchParams } from "react-router";

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

  return (
    <Card>
      <CardHeader>
        <CardTitle className="mb-2 text-2xl">Partei Filter</CardTitle>
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
    </Card>
  );
};

export default PartyFilterSection;
