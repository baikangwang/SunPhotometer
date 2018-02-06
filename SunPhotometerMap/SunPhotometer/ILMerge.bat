off echo
echo build
%1Ilmerge.exe /log %2SunPhotometer.exe /log %2BMap.NET.dll /log %2BMap.NET.WindowsForm.dll /log %2Csv.dll /log %2Newtonsoft.json.dll /log %2Nlog.dll /t:winexe /out:%2sunphotometer_map.exe /targetplatform:v4 /ndebug
echo clean up
if exist %2SunPhotometer.exe del %2SunPhotometer.*
if exist %2BMap.NET.dll del %2BMap.NET.*
if exist %2BMap.NET.WindowsForm.dll del %2BMap.NET.WindowsForm.*
if exist %2Csv.dll del %2Csv.*
if exist %2Newtonsoft.Json.dll del %2Newtonsoft.Json.*
if exist %2Nlog.dll del %2Nlog.*