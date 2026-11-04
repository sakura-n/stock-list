# 一键构建「囤货清单」桌面版（Windows）
# 产物：stock-list-backend\dist-desktop\StockList\StockList.exe（自带精简 JRE，双击即开，自动弹浏览器）
$ErrorActionPreference = "Stop"

$ROOT     = $PSScriptRoot
$FE       = Join-Path $ROOT "stock-list-miniprogram"
$BE       = Join-Path $ROOT "stock-list-backend"
$JAR_NAME = "stock-list-backend-0.0.1-SNAPSHOT.jar"
$APP_NAME = "StockList"

# 锁定 JDK 21：Lombok 1.18.34 不兼容 JDK 25，否则 @Slf4j/@Data 失效报“找不到符号”
function Test-Jdk21($home21) {
  if (-not $home21) { return $false }
  $javac = Join-Path $home21 "bin\javac.exe"
  if (-not (Test-Path $javac)) { return $false }
  ((& $javac -version 2>&1) -match '\b21\b')
}
if (-not (Test-Jdk21 $env:JAVA_HOME)) {
  $cand = Get-ChildItem "C:\Program Files\Eclipse Adoptium\jdk-21*","C:\Program Files\Java\jdk-21*" -ErrorAction SilentlyContinue | Select-Object -First 1
  if ($cand) { $env:JAVA_HOME = $cand.FullName } else {
    Write-Error "未找到 JDK 21。请安装 Adoptium/Temurin JDK 21 或手动设置 `$env:JAVA_HOME 指向 JDK 21"
    exit 1
  }
}
$env:PATH = (Join-Path $env:JAVA_HOME "bin") + ";" + $env:PATH
Write-Host "==> 使用 JDK: $env:JAVA_HOME" -ForegroundColor Cyan
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
