import requests

def query_ollama(formatted_messages):  # NE FAIS PAS DE RE-FORMATAGE
    try:
        response = requests.post(
            "http://localhost:11434/api/chat",
            headers={"Content-Type": "application/json"},
            json={
                "model": "llama3",
                "messages": formatted_messages,
                "stream": False
            },
            timeout=120
        )
        response.raise_for_status()
        return response.json()["message"]["content"]
    except Exception as e:
        return f"⚠️ LLM error: {e}"
