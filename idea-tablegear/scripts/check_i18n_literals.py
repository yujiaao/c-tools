#!/usr/bin/env python3
import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src" / "main" / "kotlin"
WHITELIST_FILE = ROOT / "scripts" / "i18n-literal-whitelist.txt"

RULES = [
    ("JBLabel literal", re.compile(r'JBLabel\(\s*"[^"]+"')),
    ("JButton literal", re.compile(r'JButton\(\s*"[^"]+"')),
    ("Messages.show* literal", re.compile(r'Messages\.show\w+\([^)]*"[^"]+"')),
]


def should_skip_line(line: str) -> bool:
    stripped = line.strip()
    return (
        stripped.startswith("//")
        or "TableGearBundle.message(" in line
        or stripped in ('JBLabel("")', 'JButton("")')
    )

def load_whitelist() -> set[str]:
    if not WHITELIST_FILE.exists():
        return set()
    allowed = set()
    for raw in WHITELIST_FILE.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        allowed.add(line.replace("\\", "/"))
    return allowed


def main() -> int:
    whitelist = load_whitelist()
    issues = []
    for file_path in SRC.rglob("*.kt"):
        rel = file_path.relative_to(ROOT)
        rel_posix = rel.as_posix()
        for idx, line in enumerate(file_path.read_text(encoding="utf-8").splitlines(), start=1):
            location = f"{rel_posix}:{idx}"
            if location in whitelist:
                continue
            if should_skip_line(line):
                continue
            for rule_name, pattern in RULES:
                if pattern.search(line):
                    issues.append(f"{location}: {rule_name}: {line.strip()}")

    if issues:
        print("Found hardcoded UI literals. Use TableGearBundle.message(...):")
        for item in issues:
            print(item)
        return 1

    print("No hardcoded UI literals found.")
    return 0


if __name__ == "__main__":
    sys.exit(main())

