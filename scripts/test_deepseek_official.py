import json
import os
import re
import sys
from pathlib import Path
from urllib import error, request


ROOT = Path(__file__).resolve().parents[1]
PRIVATE_CONFIG = ROOT / "server" / "ruoyi-backend" / "config" / "application-secret.yml"
DEFAULT_BASE_URL = "https://api.deepseek.com"
DEFAULT_MODEL = "deepseek-v4-pro"


def load_private_config():
    if not PRIVATE_CONFIG.exists():
        return {
            "api_key": "",
            "base_url": DEFAULT_BASE_URL,
            "model": DEFAULT_MODEL,
        }

    content = PRIVATE_CONFIG.read_text(encoding="utf-8")

    def find_value(key, default=""):
        pattern = rf"^\s*{re.escape(key)}:\s*(.*?)\s*$"
        match = re.search(pattern, content, flags=re.MULTILINE)
        if not match:
            return default
        value = match.group(1).strip()
        if value.startswith(("'", '"')) and value.endswith(("'", '"')) and len(value) >= 2:
            value = value[1:-1]
        return value

    return {
        "api_key": find_value("api-key", ""),
        "base_url": find_value("base-url", DEFAULT_BASE_URL) or DEFAULT_BASE_URL,
        "model": find_value("model", DEFAULT_MODEL) or DEFAULT_MODEL,
    }


def normalize_chat_completions_url(base_url):
    normalized = (base_url or DEFAULT_BASE_URL).strip().rstrip("/")
    if normalized.endswith("/chat/completions"):
        return normalized
    if normalized.endswith("/v1"):
        return normalized + "/chat/completions"
    return normalized + "/chat/completions"


def build_payload(model):
    return {
        "model": model,
        "max_tokens": 256,
        "temperature": 0,
        "messages": [
            {
                "role": "system",
                "content": "You are a concise assistant. Reply with exactly one short sentence confirming the API works.",
            },
            {
                "role": "user",
                "content": "Reply with: API test success.",
            },
        ],
    }


def post_json(url, api_key, payload):
    body = json.dumps(payload).encode("utf-8")
    req = request.Request(
        url=url,
        data=body,
        method="POST",
        headers={
            "content-type": "application/json",
            "Authorization": f"Bearer {api_key}",
        },
    )
    with request.urlopen(req, timeout=60) as resp:
        return resp.status, resp.read().decode("utf-8", errors="replace")


def main():
    config = load_private_config()
    api_key = os.environ.get("DEEPSEEK_API_KEY", config["api_key"]).strip()
    base_url = os.environ.get("DEEPSEEK_BASE_URL", config["base_url"]).strip() or DEFAULT_BASE_URL
    model = os.environ.get("DEEPSEEK_MODEL", config["model"]).strip() or DEFAULT_MODEL
    url = normalize_chat_completions_url(base_url)
    payload = build_payload(model)

    print("Testing DeepSeek official chat completions endpoint")
    print(f"Config file: {PRIVATE_CONFIG}")
    print(f"URL: {url}")
    print(f"Model: {model}")
    print(f"API key present: {'yes' if api_key else 'no'}")

    if not api_key:
        print("ERROR: api-key is empty. Fill server/ruoyi-backend/config/application-secret.yml first.")
        return 2

    try:
        status, response_text = post_json(url, api_key, payload)
        print(f"HTTP status: {status}")
        print("Raw response:")
        print(response_text)
        try:
            data = json.loads(response_text)
        except json.JSONDecodeError:
            return 0

        choices = data.get("choices") or []
        if isinstance(choices, list):
            for item in choices:
                content = ((item or {}).get("message") or {}).get("content")
                if content:
                    print("Extracted text:")
                    print(content)
                    break
        return 0
    except error.HTTPError as exc:
        body = exc.read().decode("utf-8", errors="replace")
        print(f"HTTPError: {exc.code}")
        print(body)
        return 1
    except Exception as exc:
        print(f"Request failed: {exc}")
        return 1


if __name__ == "__main__":
    sys.exit(main())
