@echo off
set base_dir=C:\SunPhotometer

echo --- Uninstall AOD scripts ---

if exist %base_dir%\SunPhotometer\CalFile (
    echo     Backup the existing CalFiles
    move %base_dir%\SunPhotometer\CalFile %base_dir%\CalFile
    rmdir /q /s %base_dir%\SunPhotometer
    mkdir %base_dir%\SunPhotometer
    move %base_dir%\CalFile %base_dir%\SunPhotometer\
)

del %base_dir%\run.*

echo --- Clean Stations ---
set data_dir=%base_dir%\data\AOD
if exist %data_dir%\stations_aod.csv del %data_dir%\stations_aod.csv

echo --- Clean Configuration ---
del %base_dir%\*.json

echo --- Uninstall Scheduled Task ---
rem https://www.howtogeek.com/51236/how-to-create-modify-and-delete-scheduled-tasks-from-the-command-line/
rem Create 'AOD' to run C:\SunPhotometer\run.bat at 9 AM everyday
SchTasks /query /TN "AOD" >NUL 2>&1 && SchTasks /Delete /TN "AOD"

echo --- Uninstall SunPhotometer Map ---
if exist %base_dir%\sunphotometer_map.exe del %base_dir%\sunphotometer_map.exe

set short_link=%USERPROFILE%\Desktop\SunPhotometerMap.lnk
if exist %short_link% del %short_link%

echo --- DONE! ---

pause