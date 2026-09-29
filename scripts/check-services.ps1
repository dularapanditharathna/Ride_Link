$services = @(
    @{ Name = 'Account Service'; Port = 8081 },
    @{ Name = 'Driver Service'; Port = 8082 },
    @{ Name = 'Ride Service'; Port = 8083 },
    @{ Name = 'Fare Service'; Port = 8084 }
)
foreach ($service in $services) {
    $result = Test-NetConnection -ComputerName localhost -Port $service.Port -WarningAction SilentlyContinue
    $status = if ($result.TcpTestSucceeded) { 'RUNNING' } else { 'NOT REACHABLE' }
    $color = if ($result.TcpTestSucceeded) { 'Green' } else { 'Red' }
    Write-Host ("{0,-22} port {1}: {2}" -f $service.Name, $service.Port, $status) -ForegroundColor $color
}

