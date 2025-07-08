import useStatisticsStore from "@/store/statisticsStore";
import { useEffect } from "react";
import { useSpring, animated } from "@react-spring/web";

const AnimatedNumber = ({ number }: { number: number }) => {
  const { number: animatedNumber } = useSpring({
    from: { number: 0 },
    to: { number },
    config: { tension: 170, friction: 26 },
  });

  return (
    <animated.span>{animatedNumber.to((n) => Math.round(n))}</animated.span>
  );
};

const StatisticsHeader = () => {
  const statistics = useStatisticsStore((state) => state.statistics);

  useEffect(() => {
    const setStatistics = useStatisticsStore.getState().setStatistics;
    setStatistics().catch((error) => {
      console.error("Failed to fetch statistics:", error);
    });
  }, []);

  return (
    <div className="bg-white rounded-xl p-6 shadow-md">
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
        <div>
          <div className="text-2xl font-bold text-sky-600">
            <AnimatedNumber number={statistics.plenaryCount} />
          </div>
          <div className="text-sm text-muted-foreground">Plenarsitzungen</div>
        </div>
        <div>
          <div className="text-2xl font-bold text-blue-600">
            <AnimatedNumber number={statistics.speakerCount} />
          </div>
          <div className="text-sm text-muted-foreground">Redner insgesamt</div>
        </div>
        <div>
          <div className="text-2xl font-bold text-indigo-600">
            <AnimatedNumber number={Math.round(statistics.wordCount / 1000)} />k
          </div>
          <div className="text-sm text-muted-foreground">Wörter insgesamt</div>
        </div>
        <div>
          <div className="text-2xl font-bold text-violet-600">
            <AnimatedNumber number={statistics.partyCount} />
          </div>
          <div className="text-sm text-muted-foreground">Parteien</div>
        </div>
      </div>
    </div>
  );
};

export default StatisticsHeader;
