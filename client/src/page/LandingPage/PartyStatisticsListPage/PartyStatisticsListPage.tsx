import usePartyStatisticsStore from "@/store/partyStatisticStore";
import {useEffect} from "react";
import PartyStatisticCard from "./components/PartyStatisticCard";
import {useNavigate} from "react-router";
import type {PartyStatisticDto} from "@/types/PartyStatisticDto";

const PartyStatisticsListPage = () => {
    const {parties, loading, error, fetchParties} =
        usePartyStatisticsStore();

    useEffect(() => {
        fetchParties();
    }, [fetchParties]);

    const navigate = useNavigate();

    const handleClick = (party: PartyStatisticDto) => {
        // Navigate to speeches filtered by party
        navigate(`/partei/${encodeURIComponent(party.party)}`);
    };

    if (error) return <div>Error: {error}</div>;

    return (
        <div className="flex flex-col gap-6 w-full">
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
                {parties.map((p) => (
                    <PartyStatisticCard
                        key={p.party}
                        party={p}
                        onClick={() => handleClick(p)}
                    />
                ))}
            </div>
            {loading && (
                <div className="flex flex-col items-center justify-center w-full py-4">
                    <p>Loading...</p>
                </div>
            )}
        </div>
    );
};

export default PartyStatisticsListPage;
