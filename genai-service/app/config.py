MODEL_CONFIG = {
    "llm_model": "gemini-2.5-flash-preview-05-20",
    "embedding_model": "models/gemini-embedding-exp-03-07",
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