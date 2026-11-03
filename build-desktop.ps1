# 一键构建「囤货清单」桌面版（Windows）
# 产物：stock-list-backend\dist-desktop\StockList\StockList.exe（自带精简 JRE，双击即开，自动弹浏览器）
$ErrorActionPreference = "Stop"

$ROOT     = $PSScriptRoot
$FE       = Join-Path $ROOT "stock-list-miniprogram"
$BE       = Join-Path $ROOT "stock-list-backend"
$JAR_NAME = "stock-list-backend-0.0.1-SNAPSHOT.jar"
$APP_NAME = "StockList"

Write-Host "==> 1/5 构建前端 H5" -ForegroundColor Cyan
Set-Location $FE
if (-not (Test-Path "$FE\node_modules")) { npm install }
npm run build:h5
if ($LASTEXITCODE -ne 0) { throw "前端构建失败" }

Write-Host "==> 2/5 将 H5 产物内嵌到后端 static" -ForegroundColor Cyan
$STATIC = Join-Path $BE "src\main\resources\static"
if (Test-Path $STATIC) { Remove-Item -Recurse -Force $STATIC }
New-Item -ItemType Directory -Force -Path $STATIC | Out-Null
Copy-Item -Recurse -Force (Join-Path $FE "dist\build\h5\*") $STATIC

Write-Host "==> 3/5 打包后端 fat jar" -ForegroundColor Cyan
Set-Location $BE
mvn -B -q package -DskipTests
if ($LASTEXITCODE -ne 0) { throw "后端打包失败" }

Write-Host "==> 4/5 jpackage 生成桌面 app-image" -ForegroundColor Cyan
$DESK = Join-Path $BE "dist-desktop"
if (Test-Path $DESK) { Remove-Item -Recurse -Force $DESK }
New-Item -ItemType Directory -Force -Path $DESK | Out-Null
$JPKG = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin\jpackage.exe" } else { "jpackage" }
& $JPKG `
  --type app-image `
  --name $APP_NAME `
  --input "$BE\target" `
  --main-jar $JAR_NAME `
  --dest $DESK `
  --java-options "-Dapp.open-browser=true" `
  --add-modules ALL-MODULE-PATH
if ($LASTEXITCODE -ne 0) { throw "jpackage 失败" }

Write-Host "==> 5/5 完成" -ForegroundColor Green
Write-Host "可执行文件： $DESK\$APP_NAME\$APP_NAME.exe"
