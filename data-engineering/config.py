import os
from dotenv import load_dotenv

load_dotenv()

DB_CONFIG = {
    "host": os.getenv("DB_HOST"),
    "port": os.getenv("DB_PORT"),
    "database": os.getenv("DB_NAME"),
    "user": os.getenv("DB_USER"),
    "password": os.getenv("DB_PASSWORD"),
}

# Validate required environment variables
_required_vars = ["host", "port", "database", "user", "password"]
_missing = [k for k in _required_vars if not DB_CONFIG[k]]

if _missing:
    raise ValueError(
        f"Missing required environment variables: "
        f"{', '.join('DB_' + k.upper() for k in _missing)}. "
        "Set them in .env or export them."
    )

DATABASE_URL = (
    f"postgresql://{DB_CONFIG['user']}:{DB_CONFIG['password']}"
    f"@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}"
)