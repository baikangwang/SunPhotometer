rem examples
rem https://www.howtogeek.com/51236/how-to-create-modify-and-delete-scheduled-tasks-from-the-command-line/

rem Create 'My Task' to run C:RunMe.bat at 9 AM everyday
SchTasks /Create /SC DAILY /TN "My Task" /TR "C:RunMe.bat" /ST 09:00

rem Modify 'My Task' to run at 2 PM
SchTasks /Change /TN "My Task" /ST 14:00

rem Create 'My Task' to run C:RunMe.bat on the first of every month
SchTasks /Create /SC MONTHLY /D 1 /TN "My Task" /TR "C:RunMe.bat" /ST 14:00

rem Create 'My Task' to run C:RunMe.bat every weekday at 2 PM
SchTasks /Create /SC WEEKLY /D MON,TUE,WED,THU,FRI /TN "My Task" /TR "C:RunMe.bat" /ST 14:00

rem Delete the task named 'My Task'
SchTasks /Delete /TN "My Task"

rem Bulk Creation
SchTasks /Create /SC DAILY /TN "Backup Data" /TR "C:Backup.bat" /ST 07:00
SchTasks /Create /SC WEEKLY /D MON /TN "Generate TPS Reports" /TR "C:GenerateTPS.bat" /ST 09:00
SchTasks /Create /SC MONTHLY /D 1 /TN "Sync Database" /TR "C:SyncDB.bat" /ST 05:00

rem shortcut
rem https://superuser.com/questions/455364/how-to-create-a-shortcut-using-a-batch-script
@echo off

set SCRIPT="%TEMP%\%RANDOM%-%RANDOM%-%RANDOM%-%RANDOM%.vbs"

echo Set oWS = WScript.CreateObject("WScript.Shell") >> %SCRIPT%
echo sLinkFile = "%USERPROFILE%\Desktop\myshortcut.lnk" >> %SCRIPT%
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> %SCRIPT%
echo oLink.TargetPath = "D:\myfile.extension" >> %SCRIPT%
echo oLink.Save >> %SCRIPT%

cscript /nologo %SCRIPT%
del %SCRIPT%

oLink.Arguments
oLink.Description
oLink.HotKey
oLink.IconLocation
oLink.WindowStyle
oLink.WorkingDirectory