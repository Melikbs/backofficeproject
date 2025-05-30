import httpx
from bs4 import BeautifulSoup

HEADERS = {
    "User-Agent": "Mozilla/5.0"
}

async def scrape_prices(product_name: str, max_results: int = 5):
    query = f"{product_name} site:jumia.com.tn OR site:mytek.tn OR site:technozone.tn"
    url = f"https://html.duckduckgo.com/html/?q={query.replace(' ', '+')}"

    async with httpx.AsyncClient(headers=HEADERS, timeout=10) as client:
        response = await client.get(url)
        response.raise_for_status()

    soup = BeautifulSoup(response.text, "html.parser")
    results = []

    for link in soup.select(".result__a", limit=max_results):
        title = link.get_text()
        href = link.get("href")
        results.append({"title": title, "url": href})

    return results

