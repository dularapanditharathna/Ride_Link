$ErrorActionPreference = 'Stop'

$mysqlCommand = Get-Command mysql -ErrorAction SilentlyContinue
if (-not $mysqlCommand) {
    Write-Error 'MySQL command-line client (mysql.exe) was not found on PATH. Add its bin folder to PATH and try again.'
    exit 1
}

$runningPorts = @(Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue |
    Where-Object { $_.LocalPort -in @(8081, 8082, 8083, 8084) } |
    Select-Object -ExpandProperty LocalPort -Unique)
if ($runningPorts.Count -gt 0) {
    Write-Error "Stop the RideLink services before resetting databases. Ports still listening: $($runningPorts -join ', ')."
    exit 1
}

Write-Host 'This permanently deletes all data in these four databases:' -ForegroundColor Yellow
Write-Host '  ridelink_account_db, ridelink_driver_db, ridelink_ride_db, ridelink_fare_db'
$confirmation = Read-Host 'Type RESET to continue'
if ($confirmation -cne 'RESET') {
    Write-Host 'Cancelled. No databases were changed.'
    exit 0
}

$mysqlUser = if ($env:MYSQL_USERNAME) { $env:MYSQL_USERNAME } else { 'root' }
$oldPassword = $env:MYSQL_PWD
$securePassword = Read-Host "Enter MySQL password for $mysqlUser" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
try {
    $env:MYSQL_PWD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    $sql = 'DROP DATABASE IF EXISTS `ridelink_account_db`; DROP DATABASE IF EXISTS `ridelink_driver_db`; DROP DATABASE IF EXISTS `ridelink_ride_db`; DROP DATABASE IF EXISTS `ridelink_fare_db`;'
    & $mysqlCommand.Source --host=localhost --port=3306 "--user=$mysqlUser" "--execute=$sql"
    if ($LASTEXITCODE -ne 0) {
        throw "MySQL reset failed with exit code $LASTEXITCODE."
    }
    Write-Host 'RideLink databases reset. Start each service again; its database and tables will be recreated automatically.' -ForegroundColor Green
} finally {
    $env:MYSQL_PWD = $oldPassword
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    $securePassword.Dispose()
}
