#!/usr/bin/env bash
# 本地快速构建（安装到本地仓库，不签名、不发布 Central）
mvn -o clean package -Dmaven.test.skip=true install -Ppro

# 发布到 Maven Central（需 ~/.m2/settings.xml 的 central 凭证 + 本机 GPG 私钥）
# mvn clean deploy -Ppro,central-publish -Dgpg.passphrase=你的GPG口令
