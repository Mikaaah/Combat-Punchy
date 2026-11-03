#!/usr/bin/env python3
"""Build against an existing NeoForge development classpath, without fetching dependencies."""
import argparse
import os
from pathlib import Path
import subprocess
import shutil
import tomllib
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--java-home', required=True, type=Path)
    parser.add_argument('--classpath-file', required=True, type=Path,
                        help='UTF-8 file with one resolved development JAR path per line')
    parser.add_argument('--epic-fight-jar', required=True, type=Path)
    args = parser.parse_args()
    root = Path(__file__).resolve().parent.parent
    suffix = '.exe' if os.name == 'nt' else ''
    javac = args.java_home / 'bin' / ('javac' + suffix)
    java = args.java_home / 'bin' / ('java' + suffix)
    classpath = [Path(s.strip()).resolve() for s in args.classpath_file.read_text().splitlines() if s.strip()]
    classpath.append(args.epic_fight_jar.resolve())
    for entry in classpath:
        if not entry.is_file():
            raise SystemExit('Missing classpath entry: ' + str(entry))
    classes = root / 'build/classes/java/main'
    tests = root / 'build/classes/java/test'
    shutil.rmtree(classes, ignore_errors=True)
    shutil.rmtree(tests, ignore_errors=True)
    classes.mkdir(parents=True, exist_ok=True)
    tests.mkdir(parents=True, exist_ok=True)
    subprocess.run([str(javac), '-source', '21', '-target', '21', '-proc:none', '-encoding', 'UTF-8',
                    '-cp', os.pathsep.join(map(str, classpath)), '-d', str(classes)]
                   + [str(p) for p in sorted((root / 'src/main/java').rglob('*.java'))], check=True)
    subprocess.run([str(javac), '-source', '21', '-target', '21', '-encoding', 'UTF-8',
                    '-cp', str(classes), '-d', str(tests)]
                   + [str(p) for p in sorted((root / 'src/test/java').rglob('*.java'))], check=True)
    for test_class in ['ModeSessionTest', 'FirstPersonHandoffTest', 'CombatIdleTimeoutTest']:
        subprocess.run([str(java), '-cp', str(classes) + os.pathsep + str(tests),
                        'nl.tnk.combatpunchy.' + test_class], check=True)
    metadata = tomllib.loads((root / 'src/main/resources/META-INF/neoforge.mods.toml').read_text())
    version = metadata['mods'][0]['version']
    jar = root / f'build/libs/combat-punchy-{version}-neoforge-1.21.1.jar'
    jar.parent.mkdir(parents=True, exist_ok=True)

    def put(archive, name, data):
        info = zipfile.ZipInfo(name, (1980, 1, 1, 0, 0, 0))
        info.compress_type = zipfile.ZIP_DEFLATED
        archive.writestr(info, data)

    with zipfile.ZipFile(jar, 'w') as archive:
        put(archive, 'META-INF/MANIFEST.MF', ('Manifest-Version: 1.0\nImplementation-Title: Combat Punchy\n'
                                          f'Implementation-Version: {version}\n\n').encode())
        for directory in [classes, root / 'src/main/resources']:
            for path in sorted(directory.rglob('*')):
                if path.is_file():
                    put(archive, path.relative_to(directory).as_posix(), path.read_bytes())
    print('Built:', jar)


if __name__ == '__main__':
    main()
