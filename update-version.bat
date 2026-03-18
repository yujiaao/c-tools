export GPG_TTY=$(tty)

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit

mvn clean install

mvn clean deploy -Ppro


mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0 versions:commit



widows:
mvn build-helper:parse-version versions:set -DnewVersion='${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion}' versions:commit
mvn build-helper:parse-version versions:set -DnewVersion='1.16.4.5' versions:commit
mvn -DskipTests=true clean deploy -Pauto -D'maven.test.skip=true'
mvn -DskipTests=true clean install -Pauto -D'maven.test.skip=true'

REM 需手动发布到中央仓库 => https://central.sonatype.com/publishing/deployments
REM need manual steps to release to central repo => https://central.sonatype.com/publishing/deployments