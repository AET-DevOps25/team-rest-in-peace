import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router";
import LandingPage from "./page/LandingPage/LandingPage.tsx";
import PlenarySessionListPage from "./page/LandingPage/PlenarySessionListPage/PlenarySessionListPage.tsx";
import CategoryLayout from "./components/layouts/CategoryLayout.tsx";
import SpeakerStatisticsListPage from "./page/LandingPage/SpeakerStatisticsListPage/SpeakerStatisticsListPage.tsx";
import PartyStatisticsListPage from "./page/LandingPage/PartyStatisticsListPage/PartyStatisticsListPage.tsx";
import SpeechListPage from "./page/LandingPage/SpeechListPage/SpeechListPage.tsx";
import GenerallLayout from "./components/layouts/GenerallLayout.tsx";
import UnsubscribePage from "./page/UnsubscribePage/UnsubscribePage.tsx";
import SpeechSearchPage from "@/page/LandingPage/SpeechSearchPage/SpeechSearchPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <LandingPage />,
    children: [
      {
        path: "/",
        element: (
          <CategoryLayout
            title="Aktuelle Plenarsitzungen"
            subtitle="Klicken Sie auf eine Sitzung, um alle Reden und Details anzuzeigen"
          >
            <PlenarySessionListPage />
          </CategoryLayout>
        ),
      },
      {
        path: "partei",
        element: (
          <CategoryLayout
            title="Parteien im Überblick"
            subtitle="Entdecken Sie Reden und Statistiken der Parteien"
          >
            <PartyStatisticsListPage />
          </CategoryLayout>
        ),
      },
      {
        path: "redner",
        element: (
          <CategoryLayout
            title="Politiker im Überblick"
            subtitle="Entdecken Sie Reden und Statistiken einzelner Politiker"
          >
            <SpeakerStatisticsListPage />
          </CategoryLayout>
        ),
      },
      {
        path: "reden",
        element: (
          <CategoryLayout
            title="Fortgeschrittene Redensuche"
            subtitle="Durchsuchen Sie Reden mit natürlicher Sprache und erweiterten Filtern"
          >
            <SpeechSearchPage />
          </CategoryLayout>
        ),
      },
    ],
  },
  {
    path: "protokolle/:plenaryProtocolId",
    element: (
      <GenerallLayout>
        <SpeechListPage />
      </GenerallLayout>
    ),
  },
  {
    path: "redner/:speakerId",
    element: (
      <GenerallLayout>
        <SpeechListPage />
      </GenerallLayout>
    ),
  },
  {
    path: "partei/:partyName",
    element: (
      <GenerallLayout>
        <SpeechListPage />
      </GenerallLayout>
    ),
  },
  {
    path: "unsubscribe",
    element: <UnsubscribePage />,
  },
]);

createRoot(document.getElementById("root")!).render(
  <RouterProvider router={router} />
);
