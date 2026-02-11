export GPG_TTY=$(tty)

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit

mvn clean install

mvn clean deploy -Ppro -D'skipTests=true'

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0 versions:commit

@ windows:
mvn build-helper:parse-version versions:set -D'newVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.1' versions:commit

