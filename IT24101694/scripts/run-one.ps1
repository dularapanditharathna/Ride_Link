param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('account', 'driver', 'ride', 'fare')]
    [string]$Service
)

$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path

$javaCommand = Get-Command java -ErrorAction SilentlyContinue
$javacCommand = Get-Command javac -ErrorAction SilentlyContinue
if (-not $javaCommand -or -not $javacCommand) {
    Write-Host 'RideLink requires JDK 21. Confirm that java --version and javac -version both report 21.' -ForegroundColor Red
    exit 1
}
$javaVersion = (& java --version | Select-Object -First 1)
$javacVersion = (& javac -version 2>&1 | Select-Object -First 1)
if ($javaVersion -notmatch '^(?:openjdk|java)\s+21(?:\.|\s|$)' -or $javacVersion -notmatch '^javac\s+21(?:\.|\s|$)') {
    Write-Host "RideLink requires JDK 21. Found '$javaVersion' and '$javacVersion'." -ForegroundColor Red
    exit 1
}

if (-not $env:MYSQL_USERNAME) { $env:MYSQL_USERNAME = 'root' }
if (-not $env:MYSQL_PASSWORD) {
    $securePassword = Read-Host 'Enter MySQL password' -AsSecureString
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    try {
        $env:MYSQL_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
        $securePassword.Dispose()
    }
}

if (-not $env:JWT_SECRET) {
    $jwtBytes = New-Object byte[] 48
    $random = [Security.Cryptography.RandomNumberGenerator]::Create()
    try {
        $random.GetBytes($jwtBytes)
    } finally {
        $random.Dispose()
    }
    $env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
}

try {
    $keyLength = [Convert]::FromBase64String($env:JWT_SECRET).Length
} catch {
    Write-Host 'JWT_SECRET must be a Base64-encoded secret. Close this PowerShell window and run this script again.' -ForegroundColor Red
    exit 1
}
if ($keyLength -lt 32) {
    Write-Host 'JWT_SECRET must contain at least 32 decoded bytes.' -ForegroundColor Red
    exit 1
}

$services = @{
    account = @{ Name = 'Account Service'; Directory = 'account-service'; Port = 8081 }
    driver  = @{ Name = 'Driver Service'; Directory = 'driver-vehicle-service'; Port = 8082 }
    ride    = @{ Name = 'Ride Service'; Directory = 'ride-management-service'; Port = 8083 }
    fare    = @{ Name = 'Fare Service'; Directory = 'fare-payment-service'; Port = 8084 }
}

$selected = $services[$Service]
$servicePath = Join-Path $projectRoot $selected.Directory
Start-Process powershell -WindowStyle Normal -WorkingDirectory $servicePath -ArgumentList '-NoExit', '-NoProfile', '-ExecutionPolicy', 'Bypass', '-Command', "& '..\mvnw.cmd' spring-boot:run"
Write-Host "$($selected.Name) is starting in a separate window: http://localhost:$($selected.Port)/swagger-ui/index.html"
Write-Host 'Run this script again from this PowerShell window to start another service with the same credentials and JWT secret.'
