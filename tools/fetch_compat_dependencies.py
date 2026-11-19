"""Fetch the unmodified upstream JARs used by the compatibility contract check."""
import hashlib
import json
import urllib.request
from pathlib import Path

root = Path(__file__).resolve().parents[1]
output = root / 'build' / 'compat-deps'
output.mkdir(parents=True, exist_ok=True)
for pin in json.loads((root / 'compat-dependencies.json').read_text()):
    target = output / pin['filename']
    if target.is_file() and hashlib.sha256(target.read_bytes()).hexdigest() == pin['sha256']:
        continue
    with urllib.request.urlopen(pin['url'], timeout=45) as response:
        data = response.read()
    if hashlib.sha256(data).hexdigest() != pin['sha256']:
        raise RuntimeError('Upstream JAR checksum mismatch: ' + pin['filename'])
    target.write_bytes(data)
print('Verified checksum-pinned Punchy and Moves upstream JARs')
