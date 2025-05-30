# test_api.py - Usage examples and test script

import requests

# API base URL
BASE_URL = "http://localhost:8000"

# Sample German plenary protocol text
SAMPLE_TEXT = """
Der Präsident: Wir treten in die Tagesordnung ein. Aufgerufen ist der Tagesordnungspunkt 1:
Erste Beratung des von der Bundesregierung eingebrachten Entwurfs eines Gesetzes zur Stärkung 
der Digitalisierung im Gesundheitswesen (Digitale-Versorgung-Gesetz - DVG).

Abgeordneter Schmidt (CDU): Herr Präsident, meine Damen und Herren! Das vorliegende Gesetz 
stellt einen wichtigen Schritt zur Modernisierung unseres Gesundheitssystems dar. Mit der 
Digitalisierung können wir die Patientenversorgung verbessern und gleichzeitig Kosten senken.

Abgeordnete Müller (SPD): Die Digitalisierung ist wichtig, aber wir müssen den Datenschutz 
gewährleisten. Patientendaten sind sensibel und müssen geschützt werden.

Abgeordneter Weber (AfD): Dieses Gesetz führt zu einer Überwachung der Patienten! Wir lehnen 
diese Datensammelwut ab!

Der Präsident: Ich schließe die Aussprache. Wir kommen zur Abstimmung...
"""


def test_summary():
    """Test the summary endpoint"""
    print("Testing Summary Endpoint...")

    payload = {"text": SAMPLE_TEXT}
    response = requests.post(f"{BASE_URL}/summary", json=payload)

    if response.status_code == 200:
        result = response.json()
        print("✓ Summary generated successfully")
        print(f"Summary: {result['summary']}")
        return result
    else:
        print(f"✗ Error: {response.status_code} - {response.text}")
        return None


def test_embedding():
    """Test the embedding endpoint"""
    print("\nTesting Embedding Endpoint...")

    payload = {"text": "Digitalisierung im Gesundheitswesen ist wichtig."}
    response = requests.post(f"{BASE_URL}/embedding", json=payload)

    if response.status_code == 200:
        result = response.json()
        print("✓ Embedding generated successfully")
        print(f"Embedding dimension: {len(result['embedding'])}")
        print(f"First 5 values: {result['embedding'][:5]}")
        return result
    else:
        print(f"✗ Error: {response.status_code} - {response.text}")
        return None


def test_combined():
    """Test the combined endpoint"""
    print("\nTesting Combined Endpoint...")

    payload = {"text": SAMPLE_TEXT}
    response = requests.post(f"{BASE_URL}/combined", json=payload)

    if response.status_code == 200:
        result = response.json()
        print("✓ Combined processing successful")
        print(f"Summary: {result['summary'][:200]}...")
        print(f"Embedding dimension: {len(result['embedding'])}")
        return result
    else:
        print(f"✗ Error: {response.status_code} - {response.text}")
        return None


if __name__ == "__main__":
    print("German Plenary Protocol API - Test Suite")
    print("=" * 50)

    try:
        test_summary()
        test_embedding()
        test_combined()

        print("\n" + "=" * 50)
        print("Test suite completed!")

    except requests.exceptions.ConnectionError:
        print("Error: Could not connect to API.")
        print("Start the server with: uvicorn main:app --reload")

# Example curl commands
print("""
Example curl commands:

# Summary
curl -X POST "http://localhost:8000/summary" \\
  -H "Content-Type: application/json" \\
  -d '{"text": "Der Bundestag debattiert..."}'

# Embedding  
curl -X POST "http://localhost:8000/embedding" \\
  -H "Content-Type: application/json" \\
  -d '{"text": "Klimapolitik ist wichtig"}'

# Combined
curl -X POST "http://localhost:8000/combined" \\
  -H "Content-Type: application/json" \\
  -d '{"text": "Parlamentarische Debatte..."}'
""")