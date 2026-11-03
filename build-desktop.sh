#!/usr/bin/env bash
# 一键构建「囤货清单」桌面版（macOS / Linux）
# 产物：dist-desktop/囤货清单.app（自带精简 JRE，双击即开，自动弹出浏览器网页）
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
FE="$ROOT/stock-list-miniprogram"
BE="$ROOT/stock-list-backend"
JAR_NAME="stock-list-backend-0.0.1-SNAPSHOT.jar"
APP_NAME="囤货清单"

echo "==> 1/5 构建前端 H5"
cd "$FE"
[ -d node_modules ] || npm install
npm run build:h5

echo "==> 2/5 将 H5 产物内嵌到后端 static"
rm -rf "$BE/src/main/resources/static"
mkdir -p "$BE/src/main/resources/static"
cp -R "$FE/dist/build/h5/." "$BE/src/main/resources/static/"

echo "==> 3/5 打包后端 fat jar"
cd "$BE"
mvn -B -q package -DskipTests

echo "==> 4/5 jpackage 生成桌面 app-image"
rm -rf "$BE/dist-desktop"
mkdir -p "$BE/dist-desktop"
JPKG="${JAVA_HOME:+$JAVA_HOME/bin/}jpackage"
"$JPKG" \
  --type app-image \
  --name "$APP_NAME" \
  --input "$BE/target" \
  --main-jar "$JAR_NAME" \
  --dest "$BE/dist-desktop" \
  --java-options "-Dapp.open-browser=true" \
  --add-modules ALL-MODULE-PATH

echo "==> 5/5 完成"
echo "可执行文件： $BE/dist-desktop/$APP_NAME.app"
echo "双击它、或执行：open \"$BE/dist-desktop/$APP_NAME.app\""
