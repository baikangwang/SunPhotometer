@echo off
set base_dir=C:\SunPhotometer
echo --- Init install directory ---

if not exist %base_dir% mkdir %base_dir%

echo --- Install AOD scripts ---
set script_dir=%base_dir%\Sunphotometer
if not exist %script_dir% (
    xcopy .\Sunphotometer\* %script_dir%\ /e/h/s/y
) else (
    if exist %script_dir%\CalFile (
        echo     Backup the existing CalFiles
        rem the last backup
        if exist %base_dir%\CalFile.backup rmdir /q /s %base_dir%\CalFile.backup
        rem make new backup
        move %script_dir%\CalFile %base_dir%\CalFile.backup
        rmdir /q /s %script_dir%
        xcopy .\Sunphotometer\* %script_dir%\ /e/h/s/y
        move %base_dir%\CalFile.backup %script_dir%\
    )
)

xcopy .\run.* %base_dir%\ /e/h/s/y

echo --- Init Stations ---
set data_dir=%base_dir%\data\AOD
if not exist %data_dir% mkdir %data_dir%
xcopy .\data\AOD\stations_aod.csv %data_dir% /e/h/s/y

echo --- Init Configuration ---
xcopy .\*.json %base_dir% /e/h/s/y

echo --- Create Scheduled Task ---
rem https://www.howtogeek.com/51236/how-to-create-modify-and-delete-scheduled-tasks-from-the-command-line/
rem Create 'AOD' to run C:\SunPhotometer\run.bat at 9 AM everyday
SchTasks /query /TN "AOD" >NUL 2>&1 && SchTasks /Delete /TN "AOD"
SchTasks /Create /SC DAILY /TN "AOD" /TR "%base_dir%\run.bat" /ST 09:00

echo --- Install SunPhotometer Map ---
xcopy .\sunphotometer_map.exe %base_dir%\ /e/h/s/y

set short_link=%USERPROFILE%\Desktop\SunPhotometerMap.lnk
if exist %short_link% del %short_link%

set SCRIPT="%TEMP%\%RANDOM%-%RANDOM%-%RANDOM%-%RANDOM%.vbs"

echo Set oWS = WScript.CreateObject("WScript.Shell") >> %SCRIPT%
echo sLinkFile = "%short_link%" >> %SCRIPT%
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> %SCRIPT%
echo oLink.TargetPath = "%base_dir%\sunphotometer_map.exe" >> %SCRIPT%
echo oLink.Save >> %SCRIPT%

cscript /nologo %SCRIPT%
del %SCRIPT%

echo --- DONE! ---

pause