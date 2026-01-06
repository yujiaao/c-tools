export GPG_TTY=$(tty)

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit

mvn clean install

mvn -f pom_ah.xml -Dmaven.test.skip=true clean deploy -Pnexus
mvn  deploy -f pom_ah.xml -Dmaven.test.skip=true -DskipTests=false

mvn   -Dmaven.test.skip=true deploy -Dmaven.test.skip=true -DskipTests=true


mvnd clean deploy -Ppro -U -Dmaven.test.skip=true
mvnd  build-helper:parse-version versions:set -DnewVersion=1.15.10.6 versions:commit
mvnd clean install -Pautohome -U -Dmaven.test.skip=true

mvn clean package -U -Dmaven.test.skip=true

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.0 versions:commit


mvnd  build-helper:parse-version versions:set -DnewVersion=1.15.10.10 versions:commit
mvn  build-helper:parse-version versions:set -DnewVersion=1.12.2.4 versions:commit
