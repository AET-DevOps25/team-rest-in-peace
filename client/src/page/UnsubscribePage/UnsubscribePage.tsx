import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import useSubscriptionStore from "@/store/subscriptionStore";
import { AlertTriangle, Check } from "lucide-react";
import { useNavigate, useSearchParams } from "react-router";

const UnsubscribePage = () => {
  const [searchParams] = useSearchParams();
  const email = searchParams.get("email");

  const navigate = useNavigate();

  const { unsubscribe, loading, error, success } = useSubscriptionStore();

  return (
    <div className="flex items-center justify-center h-screen w-screen">
      {loading ? (
        <div>Loading...</div>
      ) : success ? (
        <Card>
          <CardContent className="pt-6">
            <div className="flex flex-col items-center text-center space-y-4">
              <div className="w-16 h-16 bg-green-100 dark:bg-green-900 rounded-full flex items-center justify-center">
                <Check className="w-8 h-8 text-green-600 dark:text-green-400" />
              </div>

              <div className="space-y-2">
                <h1 className="text-2xl font-bold">Erfolgreich abgemeldet</h1>
                <p className="text-muted-foreground">
                  Alle Benachrichtigungen für <strong>{email}</strong> wurden
                  deaktiviert.
                </p>
              </div>

              <div className="space-y-2 pt-4">
                <a href="/" className="block">
                  <Button className="w-full">Zurück zur Startseite</Button>
                </a>
              </div>
            </div>
          </CardContent>
        </Card>
      ) : email && !error ? (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <AlertTriangle className="w-5 h-5 text-orange-500" />
              Abmeldung bestätigen
            </CardTitle>
            <CardDescription>
              Bist du dir sicher, dass du keine Benachrichtigungen mehr bekommen
              möchtest?
            </CardDescription>
          </CardHeader>

          <CardContent className="space-y-4">
            <Alert>
              <AlertTriangle className="h-4 w-4" />
              <AlertDescription>
                <strong>Info:</strong> "Ja" zu drücken wird alle deine
                bestehenden E-Mail-Subscriptions aufheben für die Adresse:{" "}
                <strong>{email}</strong>
              </AlertDescription>
            </Alert>

            <div className="flex gap-2 pt-4">
              <Button
                variant="outline"
                className="flex-1 bg-transparent"
                onClick={() => navigate("/")}
              >
                Abbrechen
              </Button>
              <Button
                variant="destructive"
                className="flex-1"
                onClick={() => unsubscribe(email || "")}
              >
                {"Ja, abmelden"}
              </Button>
            </div>

            <div className="mt-6 p-4 bg-muted rounded-lg">
              <h3 className="font-semibold text-sm mb-2">
                Was passiert beim Abmelden?
              </h3>
              <ul className="text-sm text-muted-foreground space-y-1">
                <li>
                  • Alle Benachrichtigungen für Plenarsitzungen werden
                  deaktiviert
                </li>
                <li>• Alle Partei-Benachrichtigungen werden entfernt</li>
                <li>• Alle Politiker-Benachrichtigungen werden gestoppt</li>
                <li>• Sie können sich jederzeit wieder anmelden</li>
              </ul>
            </div>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <AlertTriangle className="w-5 h-5 text-red-500" />
              Fehler
            </CardTitle>
          </CardHeader>
          <CardContent>
            <Alert variant="destructive">
              <AlertTriangle className="h-4 w-4" />
              <AlertDescription>
                {error
                  ? error
                  : "Keine E-Mail-Adresse in der URL gefunden. Bitte verwenden Sie den Link aus Ihrer E-Mail."}
              </AlertDescription>
            </Alert>

            <div className="mt-6 p-4 bg-muted rounded-lg">
              <h3 className="font-semibold text-sm mb-2">
                So funktioniert die Abmeldung:
              </h3>
              <ul className="text-sm text-muted-foreground space-y-1">
                <li>• Verwenden Sie den Abmelde-Link aus Ihrer E-Mail</li>
                <li>• Der Link enthält Ihre E-Mail-Adresse automatisch</li>
                <li>• Keine manuelle Eingabe erforderlich</li>
              </ul>
            </div>

            <a href="/" className="block mt-4">
              <Button className="w-full">Zurück zur Startseite</Button>
            </a>
          </CardContent>
        </Card>
      )}
    </div>
  );
};

export default UnsubscribePage;
