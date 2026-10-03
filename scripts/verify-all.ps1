$ErrorActionPreference = "Stop"
Write-Host "Cinema One - full Phase 2 verification" -ForegroundColor Cyan
mvn clean test
if ($LASTEXITCODE -ne 0) {
    Write-Error "Verification failed. Do not start or deploy this build."
    exit $LASTEXITCODE
}
Write-Host "All Maven tests passed." -ForegroundColor Green
Write-Host "You can now run: mvn spring-boot:run" -ForegroundColor Green
