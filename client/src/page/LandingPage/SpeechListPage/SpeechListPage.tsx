import {useInfiniteScroll} from "@/hooks/useInfiniteScroll";
import useSpeechStore from "@/store/speechStore";
import {useEffect, useState} from "react";
import SpeechCard from "./components/SpeechCard";
import PartyFilterSection from "./components/PartyFilterSection";
import {useParams, useSearchParams} from "react-router";
import {ArrowLeft} from "lucide-react";

const SpeechListPage = () => {
    const {
        speeches,
        loading,
        error,
        fetchSpeeches,
        page,
        totalPages,
        getPlenaryProtocolName,
        getSpeakerName,
    } = useSpeechStore();

    const [searchParams] = useSearchParams();
    const partyFromQuery = searchParams.get("party");
    const {speakerId, plenaryProtocolId, partyName} = useParams();

    const speakerIdParsed = speakerId ? parseInt(speakerId, 10) : undefined;
    const plenaryProtocolIdParsed = plenaryProtocolId
        ? parseInt(plenaryProtocolId, 10)
        : undefined;

    // Determine which party to use for filtering
    const party = partyName || partyFromQuery || undefined;

    const [title, setTitle] = useState(
        speakerId ? `Reden` : partyName ? `Reden der ${partyName}` : `Protokolle der Sitzung`
    );

    useEffect(() => {
        fetchSpeeches(0, 10, false, {
            parties: party ? [party] : undefined,
            speakerIds: speakerIdParsed ? [speakerIdParsed] : undefined,
            plenaryProtocolId: plenaryProtocolIdParsed,
        });

        if (plenaryProtocolIdParsed) {
            getPlenaryProtocolName(plenaryProtocolIdParsed).then(setTitle);
        }

        if (speakerIdParsed) {
            getSpeakerName(speakerIdParsed).then(setTitle);
        }
    }, [
        fetchSpeeches,
        party,
        speakerIdParsed,
        plenaryProtocolIdParsed,
        getPlenaryProtocolName,
        getSpeakerName,
    ]);

    const observerRef = useInfiniteScroll({
        loading,
        hasMore: page + 1 < totalPages,
        onLoadMore: () =>
            fetchSpeeches(page + 1, 10, true, {
                parties: party,
                speakerIds: speakerIdParsed ? [speakerIdParsed] : undefined,
                plenaryProtocolId: plenaryProtocolIdParsed,
            }),
    });

    if (error) return <div>Error: {error}</div>;

    return (
        <div className="flex flex-col gap-6 w-full">
            {(speakerId || plenaryProtocolId || partyName) && (
                <div className="mb-4">
                    <a
                        href="/"
                        className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground mb-4"
                    >
                        <ArrowLeft className="w-4 h-4"/>
                        Zurück zur Übersicht
                    </a>
                    <h1 className="text-3xl font-bold">{title}</h1>
                </div>
            )}
            {!speakerId && !partyName && <PartyFilterSection/>}
            {speeches.map((s, i) => (
                <SpeechCard key={`${s.firstName}-${s.lastName}-${i}`} speech={s}/>
            ))}
            {loading && (
                <div className="flex flex-col items-center justify-center w-full py-4">
                    <p>Loading...</p>
                </div>
            )}
            <div ref={observerRef}/>
        </div>
    );
};

export default SpeechListPage;
