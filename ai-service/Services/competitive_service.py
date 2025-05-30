import httpx
from bs4 import BeautifulSoup
from urllib.parse import urljoin
import re
import requests
from difflib import SequenceMatcher
from utils.db import get_product_name_by_id

HEADERS = {
    "User-Agent": "Mozilla/5.0"
}

SITES = {
    "mytek.tn": "https://www.mytek.tn/recherche?controller=search&s={query}",
    "tunisiatech.tn": "https://tunisiatech.tn/recherche?controller=search&s={query}",
    "samsungtunisie.tn": "https://www.samsungtunisie.tn/fr/recherche?controller=search&s={query}",
    "mega.tn": "https://www.mega.tn/recherche?search_query={query}",
    "tunisianet.com.tn": "https://www.tunisianet.com.tn/recherche?controller=search&search_query={query}",
    "technopro-online.com": "https://www.technopro-online.com/index.php?controller=search&search_query={query}"
}

PRICE_SELECTORS = {
    "mytek.tn": ".price, .product-price, .current-price",
    "tunisiatech.tn": "meta[itemprop='price'], .price, .product-price, .current-price",
    "samsungtunisie.tn": "span[itemprop='price'], .price",
    "mega.tn": ".price, span.price",
    "tunisianet.com.tn": "span.price",
    "technopro-online.com": ".price"
}

def clean_price(raw: str) -> str:
    try:
        raw = raw.strip().replace("\xa0", "").replace("\u202f", "")
        num = float(re.findall(r"\d+[.,]?\d*", raw)[0].replace(",", "."))
        if 300 <= num <= 10000:
            return f"{num:,.3f}".replace(",", " ") + " DT"
    except Exception as e:
        print(f"⚠️ clean_price error: {e} in raw: {raw}")
    return "Prix non trouvé"

def is_similar(title: str, product_name: str) -> bool:
    title = title.lower()
    product_name = product_name.lower()

    title_words = re.findall(r"\w+", title)
    product_words = [w for w in re.findall(r"\w+", product_name) if len(w) > 2]

    matched = 0
    for word in product_words:
        if any(SequenceMatcher(None, word, tw).ratio() > 0.8 for tw in title_words):
            matched += 1

    if len(title_words) < 4:
        return False

    return matched >= max(2, int(0.6 * len(product_words)))

def extract_price(site: str, product_url: str) -> str:
    try:
        print(f"🌐 Fetching product page: {product_url}")
        response = httpx.get(product_url, headers=HEADERS, timeout=20)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, "html.parser")

        selector_group = PRICE_SELECTORS.get(site)
        if not selector_group:
            print(f"⚠️ No price selector defined for site: {site}")
            return "Prix non trouvé"

        selectors = [s.strip() for s in selector_group.split(",")]

        for sel in selectors:
            tag = soup.select_one(sel)
            if tag:
                raw_price = tag['content'] if tag.name == 'meta' and tag.get('content') else tag.get_text()
                print(f"💰 Raw price found ({sel}): {raw_price}")
                return clean_price(raw_price)

        print(f"⚠️ No price tag found using any of selectors '{selector_group}' for {site}")
    except Exception as e:
        print(f"❌ Exception while fetching price for {site}: {e}")

    return "Prix non trouvé"

def fetch_best_match(site: str, query: str) -> dict:
    try:
        search_url = SITES[site].format(query=query)
        resp = httpx.get(search_url, headers=HEADERS, timeout=20)
        soup = BeautifulSoup(resp.text, "html.parser")
        links = soup.select("a")

        for link in links:
            title = link.get("title") or link.get_text()
            href = link.get("href")
            if not href or not title:
                continue
            print(f"📝 Found title: {title}")
            if is_similar(title, query):
                full_url = urljoin(search_url, href)
                price = extract_price(site, full_url)
                print(f"💰 Matched {site}: {title} → {price}")
                if price != "Prix non trouvé":
                    return {
                        "site": site,
                        "price": price,
                        "url": full_url
                    }
    except Exception as e:
        print(f"❌ Error fetching or parsing search results for {site}: {e}")
    return None

def scrape_prices(product_name: str):
    results = []
    for site in SITES:
        print(f"🔎 Searching {site}...")
        match = fetch_best_match(site, product_name)
        if match:
            print(f"✅ Match found on {site}: {match['price']}")
            results.append(match)
    return results

def generate_sales_strategy(product_name: str, results: list) -> str:
    try:
        if not results:
            prompt = f"""
Tu es un assistant commercial pour un site e-commerce tunisien.

Le produit : **{product_name}** n’est proposé sur aucun site concurrent actuellement.

Propose une idée marketing originale : une réduction spéciale, une offre découverte, ou un pack incitatif.

Réponds comme si tu parlais à un responsable commercial. Une seule phrase, claire et persuasive.
"""
        else:
            prompt = f"""
Tu es un assistant commercial intelligent pour une boutique e-commerce tunisienne.

Produit surveillé : **{product_name}**
Offres concurrentes :
{chr(10).join([f"- {r['site']}: {r['price']} ({r['url']})" for r in results])}

Propose une stratégie pour surpasser la concurrence : offre pack, prix intelligent, ou campagne. Parle au responsable commercial, pas au client.
Réponds en une seule phrase.
"""

        response = requests.post(
            "http://localhost:11434/api/chat",
            headers={"Content-Type": "application/json"},
            json={
                "model": "llama3",
                "messages": [{"role": "user", "content": prompt}],
                "stream": False
            },
            timeout=60
        )
        response.raise_for_status()
        return response.json()["message"]["content"]
    except Exception as e:
        return f"⚠️ LLM error: {e}"

def compare_product(product_id: int):
    name = get_product_name_by_id(product_id)
    if not name:
        return {"error": "Produit introuvable dans la base de données."}

    results = scrape_prices(name)
    if not results:
        return {
            "product_id": product_id,
            "product_name": name,
            "results": [],
            "suggestion": generate_sales_strategy(name, [])
        }

    suggestion = generate_sales_strategy(name, results)

    return {
        "product_id": product_id,
        "product_name": name,
        "results": results,
        "suggestion": suggestion
    }
