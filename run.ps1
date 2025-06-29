$JAVA_HOME = "C:\Program Files\Java\openjdk-24.0.1_windows-x64"
$JAVAFX_HOME = "C:\Program Files\Java\javafx-sdk-24.0.1"

# Create output directory if it doesn't exist
New-Item -ItemType Directory -Force -Path "out" | Out-Null

Write-Host "Compiling..."
& "$JAVA_HOME\bin\javac.exe" `
    --module-path "$JAVAFX_HOME\lib" `
    --add-modules javafx.controls `
    -d out `
    src\tournament\scheduler\TournamentSchedulerGUI.java `
    src\module-info.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "Running..."
    & "$JAVA_HOME\bin\java.exe" `
        --module-path "$JAVAFX_HOME\lib" `
        --add-modules javafx.controls `
        -cp out `
        tournament.scheduler.TournamentSchedulerGUI
} else {
    Write-Host "Compilation failed!"
    Read-Host "Press Enter to exit"
}

Read-Host "Press Enter to exit" 