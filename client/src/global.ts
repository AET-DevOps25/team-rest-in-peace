export const BROWSING_BASE_URL =
  import.meta.env.VITE_BROWSING_BASE_URL || "http://localhost:8081";

export function getPartyColor(party: string) {
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

export function formatWords(words: number) {
  if (words >= 1000) {
    return `${(words / 1000).toFixed(1)}k Wörter`;
  }
  return `${words} Wörter`;
}
