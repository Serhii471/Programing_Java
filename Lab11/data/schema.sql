CREATE TABLE IF NOT EXISTS financial_institutions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL CHECK (length(trim(name)) > 0),
    license_code TEXT NOT NULL UNIQUE CHECK (length(trim(license_code)) > 0),
    base_capital REAL NOT NULL CHECK (base_capital >= 0)
);

CREATE INDEX IF NOT EXISTS idx_financial_institutions_capital
ON financial_institutions(base_capital);
