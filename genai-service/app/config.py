MODEL_CONFIG = {
    "llm_model": "gemini-2.0-flash",
    "embedding_model": "models/text-embedding-004",
}

PROMPTS = {
    "summary": """Du bist ein Experte für die Analyse deutscher Parlamentsprotokolle. 
Erstelle eine präzise, aber knappe Zusammenfassung des folgenden Plenarprotokolls.

Fokussiere dich auf:
- Hauptthemen und diskutierte Gesetzentwürfe
- Wichtige Redner und ihre Kernaussagen
- Abstimmungsergebnisse (falls vorhanden)
- Kontroverse Punkte und unterschiedliche Standpunkte
- Beschlüsse und nächste Schritte

Verwende einen sachlichen, informativen Ton. Die Zusammenfassung sollte die länge eines kurzen Paragraphen haben.

Text: {text}

Zusammenfassung:"""
}