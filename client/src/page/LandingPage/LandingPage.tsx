import { Outlet, useLocation, useNavigate } from "react-router";
import StatisticsHeader from "./components/StatisticsHeader";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

const LandingPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const TabValues = {
    PLENARPROTOKOLL: "plenarprotokoll",
    PARTEI: "partei",
    REDNER: "redner",
  } as const;
  type TabValues = (typeof TabValues)[keyof typeof TabValues];

  const path = location.pathname;
  const currentTab = path.includes("redner")
    ? TabValues.REDNER
    : path.includes("partei")
    ? TabValues.PARTEI
    : TabValues.PLENARPROTOKOLL; // default to plenarprotokoll

  const handleTabChange = (value: string) => {
    if (value === TabValues.PLENARPROTOKOLL) {
      navigate("/");
      return;
    }
    navigate(`/${value}`);
  };

  return (
    <div className="relative isolate pt-14">
      <div
        className="absolute inset-x-0 -top-40 -z-10 transform-gpu overflow-hidden blur-3xl sm:-top-80"
        aria-hidden="true"
      >
        <div
          className="relative left-[calc(50%-11rem)] aspect-1155/678 w-144.5 -translate-x-1/2 rotate-30 bg-linear-to-tr from-[#BEDBFF] to-[#9089fc] opacity-20 sm:left-[calc(50%-30rem)] sm:w-288.75"
          style={{
            clipPath:
              "polygon(74.1% 44.1%, 100% 61.6%, 97.5% 26.9%, 85.5% 0.1%, 80.7% 2%, 72.5% 32.5%, 60.2% 62.4%, 52.4% 68.1%, 47.5% 58.3%, 45.2% 34.5%, 27.5% 76.7%, 0.1% 64.9%, 17.9% 100%, 27.6% 76.8%, 76.1% 97.7%, 74.1% 44.1%)",
          }}
        />
      </div>
      <div className="flex flex-col min-w-fit max-w-[100rem] w-full h-full mx-auto p-4 gap-4">
        <div className="flex flex-col items-center text-center p-4 gap-2">
          <h1 className="text-5xl font-bold bg-gradient-to-r via-cyan-600 from-violet-800 to-blue-800 min-h-14 bg-clip-text text-transparent">
            Bundestag Reden Suche
          </h1>
          <p className="text-xl text-muted-foreground max-w-3xl ">
            Durchsuchen und analysieren Sie alle Plenarprotokolle und Reden des
            Deutschen Bundestages. Verschaffen Sie sich einen transparenten
            Überblick über politische Diskussionen und Positionen.
          </p>
        </div>
        <StatisticsHeader />

        <Tabs
          value={currentTab}
          onValueChange={handleTabChange}
          className="w-full"
        >
          <TabsList className="w-full h-fit bg-slate-200">
            <TabsTrigger
              value={TabValues.PLENARPROTOKOLL}
              className={`${
                currentTab === TabValues.PLENARPROTOKOLL
                  ? ""
                  : "text-muted-foreground"
              } p-1`}
            >
              Plenarsitzung
            </TabsTrigger>
            <TabsTrigger
              value={TabValues.PARTEI}
              className={`${
                currentTab === TabValues.PARTEI ? "" : "text-muted-foreground"
              } p-1`}
            >
              Nach Partei
            </TabsTrigger>
            <TabsTrigger
              value={TabValues.REDNER}
              className={`${
                currentTab === TabValues.REDNER ? "" : "text-muted-foreground"
              } p-1`}
            >
              Nach Redner
            </TabsTrigger>
          </TabsList>

          <TabsContent value={currentTab}>
            <div className="flex-grow overflow-auto">
              <Outlet />
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default LandingPage;
