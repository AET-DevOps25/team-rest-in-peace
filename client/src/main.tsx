import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router";
import LandingPage from "./page/LandingPage/LandingPage.tsx";
import PlenarySessionListPage from "./page/LandingPage/PlenarySessionListPage/PlenarySessionListPage.tsx";
import CategoryLayout from "./components/layouts/CategoryLayout.tsx";
import SpeakerStatisticsListPage from "./page/LandingPage/SpeakerStatisticsListPage/SpeakerStatisticsListPage.tsx";
import SpeechListPage from "./page/LandingPage/SpeechListPage/SpeechListPage.tsx";
import ProtocollSpeechListPage from "./page/ProtocollSpeechListPage/ProtocollSpeechListPage.tsx";
import GenerallLayout from "./components/layouts/GenerallLayout.tsx";

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
            title="Reden nach Parteien"
            subtitle="Durchsuchen Sie Reden sortiert nach Partei und Datum"
          >
            <SpeechListPage />
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
    path: "reden/:speakerId",
    element: (
      <GenerallLayout>
        <SpeechListPage />
      </GenerallLayout>
    ),
  },
]);

createRoot(document.getElementById("root")!).render(
  <RouterProvider router={router} />
);
