import { createRoot } from "react-dom/client";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router";
import LandingPage from "./page/LandingPage/LandingPage.tsx";
import PlenarySessionListPage from "./page/LandingPage/PlenarySessionListPage/PlenarySessionListPage.tsx";
import CategoryLayout from "./components/layouts/CategoryLayout.tsx";

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
            <div>Nach Partei</div>
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
            <div>Nach Redner</div>
          </CategoryLayout>
        ),
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <RouterProvider router={router} />
);
