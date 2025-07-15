import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { getPartyColor } from "@/global";
import { Label } from "@radix-ui/react-label";
import { Bell, Check, Mail } from "lucide-react";
import { useState } from "react";

interface NotificationModalProps {
  isOpen: boolean;
  onClose: () => void;
  type: "session" | "speaker" | "party";
  speaker?: {
    name: string;
    id: number;
    party?: string;
  };
  party?: string;
}

const NotificationModal = ({
  isOpen,
  onClose,
  type,
  speaker,
  party,
}: NotificationModalProps) => {
  const [email, setEmail] = useState("");

  const [successful, setSuccessful] = useState(false); //TODO: GET FROM STORE WHEN OTHER UI IS MERGED

  const getTitle = () => {
    switch (type) {
      case "session":
        return "Benachrichtigungen für neue Plenarsitzungen";
      case "party":
        return "Benachrichtigungen für Partei";
      case "speaker":
        return "Benachrichtigungen für Politiker";
      default:
        return "Benachrichtigungen einrichten";
    }
  };

  const getDescription = () => {
    switch (type) {
      case "session":
        return "Erhalten Sie Updates zu neuen Plenarsitzungen des Bundestages";
      case "party":
        return `Erhalten Sie Updates zu allen Reden und Aktivitäten von ${
          party ?? "der Partei"
        }`;
      case "speaker":
        return `Erhalten Sie Updates zu allen Reden von ${
          speaker?.name ?? "diesem Politiker"
        }`;
      default:
        return "Wählen Sie Ihre Benachrichtigungseinstellungen";
    }
  };

  const isValidEmail = (email: string) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  if (successful) {
    return (
      <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            Benachrichtigungen aktiviert!
          </DialogTitle>
          <DialogDescription>
            Sie erhalten ab sofort Benachrichtigungen an{" "}
            <strong>{email}</strong>
          </DialogDescription>
        </DialogHeader>
        <DialogContent className="sm:max-w-md">
          <div className="flex flex-col items-center justify-center py-8 text-center">
            <div className="w-16 h-16 bg-green-100 dark:bg-green-900 rounded-full flex items-center justify-center mb-4">
              <Check className="w-8 h-8 text-green-600 dark:text-green-400" />
            </div>
            <h3 className="text-lg font-semibold mb-2">
              Benachrichtigungen aktiviert!
            </h3>
            <p className="text-muted-foreground mb-4">
              Sie erhalten ab sofort Benachrichtigungen an{" "}
              <strong>{email}</strong>
            </p>
            <p className="text-xs text-muted-foreground">
              Sie können sich jederzeit{" "}
              <a
                href={`/unsubscribe?email=${encodeURIComponent(email)}`}
                className="underline hover:no-underline"
              >
                hier abmelden
              </a>
            </p>
          </div>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Bell className="w-5 h-5" />
            {getTitle()}
          </DialogTitle>
          <DialogDescription>{getDescription()}</DialogDescription>

          {speaker && (
            <div className="p-3 bg-muted rounded-lg">
              <div className="flex items-center gap-2">
                <strong>{speaker?.name ?? "Name not found"}</strong>
                {speaker?.name && (
                  <Badge
                    variant="outline"
                    className={`${getPartyColor(
                      speaker?.party ?? ""
                    )} text-white text-xs`}
                  >
                    {speaker?.party ?? "Unbekannt"}
                  </Badge>
                )}
              </div>
            </div>
          )}
          {party && (
            <div className="p-3 bg-muted rounded-lg">
              <div className="flex items-center gap-2">
                <Badge
                  variant="outline"
                  className={`${getPartyColor(party ?? "")} text-white text-xs`}
                >
                  {party ?? "Unbekannt"}
                </Badge>
              </div>
            </div>
          )}
        </DialogHeader>
        <div className="space-y-2">
          <Label htmlFor="email" className="flex items-center gap-2">
            <Mail className="w-4 h-4" />
            E-Mail-Adresse
          </Label>
          <Input
            id="email"
            type="email"
            placeholder="ihre.email@beispiel.de"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="flex gap-2 pt-4">
          <Button
            type="button"
            variant="outline"
            onClick={onClose}
            className="flex-1 bg-transparent"
          >
            Abbrechen
          </Button>
          <Button
            type="submit"
            className="flex-1"
            disabled={!email || !isValidEmail(email)}
          >
            Benachrichtigungen aktivieren
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default NotificationModal;
