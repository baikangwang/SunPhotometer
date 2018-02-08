@echo off
set package=.\package
if exist %package% rmdir %package% /q /s

mkdir %package%
xcopy .\data\AOD\stations_aod.csv %package%\data\AOD\ /s/y
xcopy .\Sunphotometer\* %package%\Sunphotometer\ /e/h/s/y
del %package%\Sunphotometer\*.class
copy .\*.json %package%
copy .\run.* %package%
copy .\*.exe %package%
copy .\Install.bat %package%
copy .\Uninstall.bat %package%

pause