"""Upload prebuilt Combat Punchy releases; never build or retry a POST."""
import hashlib
import json
import os
from pathlib import Path
import re
import urllib.error
import urllib.request
import uuid

CF = 'https://minecraft.curseforge.com'
PROJECT = 1779010
SLUG = 'combat-punchy-epic-fight-x-punchy-x-punchy-moves'
TOKEN = os.environ['CURSEFORGE_TOKEN']
if not TOKEN:
    raise SystemExit('Combat-Punchy repository secret CURSEFORGE_TOKEN is missing')

class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *args, **kwargs):
        return None

def request(url, data=None, content_type=None, authenticated=False):
    headers = {'User-Agent': 'Combat-Punchy-Release-Publisher/1.0', 'Accept': 'application/json'}
    if authenticated:
        assert url.startswith(CF + '/')
        headers['X-Api-Token'] = TOKEN
    if content_type:
        headers['Content-Type'] = content_type
    req = urllib.request.Request(url, headers=headers, data=data)
    try:
        with urllib.request.build_opener(NoRedirect).open(req, timeout=90) as response:
            return json.load(response)
    except urllib.error.HTTPError as exc:
        detail = exc.read(1000).decode(errors='replace').replace(TOKEN, '[redacted]')
        raise RuntimeError(f'CurseForge HTTP {exc.code}: {detail}') from None

def version_id(versions, label):
    matches = [v for v in versions if v.get('name', '').casefold() == label.casefold()]
    ids = {v['id'] for v in matches}
    if len(ids) > 1:
        native = [v for v in matches if 'apiVersion' in v and v['apiVersion'] is None]
        if len({v['id'] for v in native}) == 1:
            return native[0]['id']
    if len(ids) != 1:
        raise RuntimeError(f'Cannot uniquely resolve CurseForge label {label}')
    return ids.pop()

version = os.environ.get('RELEASE_VERSION', '1.3.2')
if not re.fullmatch(r'\d+\.\d+\.\d+', version):
    raise SystemExit('Invalid release version')
root = Path('.github/publish/releases') / version
manifest = json.loads((root / 'request.json').read_text())
assert manifest['project_id'] == PROJECT and manifest['version'] == version
changelog = (root / 'changelog.html').read_text()
assert hashlib.sha256(changelog.encode()).hexdigest() == manifest['changelog_sha256']
versions = request(CF + '/api/game/versions', authenticated=True)
existing = request(f'https://www.curseforge.com/api/v1/mods/{PROJECT}/files?pageIndex=0&pageSize=50&sort=dateCreated&sortDescending=true')['data']
plans = []
for artifact in manifest['artifacts']:
    path = root / artifact['filename']
    payload = path.read_bytes()
    assert hashlib.sha256(payload).hexdigest() == artifact['sha256'], path.name
    matches = [f for f in existing if f['fileName'] == path.name]
    if len(matches) > 1:
        raise RuntimeError(f'Duplicate remote files named {path.name}')
    metadata = {'displayName': path.name, 'releaseType': 'release', 'changelogType': 'html',
                'changelog': changelog,
                'gameVersions': [version_id(versions, label) for label in artifact['labels']],
                'relations': {'projects': manifest['relations']}}
    plans.append((artifact, payload, metadata, matches))
results = []
try:
    for artifact, payload, metadata, matches in plans:
        name = artifact['filename']
        entry = {'filename': name, 'sha256': artifact['sha256']}
        results.append(entry)
        if matches:
            entry.update(status='already_uploaded', id=matches[0]['id'])
        else:
            entry['status'] = 'uploading'
            Path('curseforge-upload-results.json').write_text(json.dumps(results, indent=2))
            boundary = 'combat-punchy-' + uuid.uuid4().hex
            body = (f'--{boundary}\r\nContent-Disposition: form-data; name="metadata"\r\nContent-Type: application/json\r\n\r\n'.encode()
                    + json.dumps(metadata).encode()
                    + f'\r\n--{boundary}\r\nContent-Disposition: form-data; name="file"; filename="{name}"\r\nContent-Type: application/java-archive\r\n\r\n'.encode()
                    + payload + f'\r\n--{boundary}--\r\n'.encode())
            uploaded = request(f'{CF}/api/projects/{PROJECT}/upload-file', body,
                               f'multipart/form-data; boundary={boundary}', authenticated=True)
            entry.update(status='uploaded', id=int(uploaded['id']))
        entry['url'] = f'https://www.curseforge.com/minecraft/mc-mods/{SLUG}/files/{entry["id"]}'
        print(json.dumps(entry), flush=True)
        with open(os.environ['GITHUB_STEP_SUMMARY'], 'a') as summary:
            summary.write(f'- [{name}]({entry["url"]}) — {entry["status"]}\n')
finally:
    Path('curseforge-upload-results.json').write_text(json.dumps(results, indent=2))
