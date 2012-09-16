@echo off
cls
echo Unpacking..
mkdir test
echo @java -classpath ..\ loadtest.loadtest ^> test\test%%1.log > test\singletest.bat
echo @exit >> test\singletest.bat
echo Starting tests..
set count=0
if [%1]==[] set total=10
if [%1] NEQ [] set total=%1 
set /a last=%total%-1
:begin
echo Starting test %count%
if %count%==%last% echo Waiting for test completion && start /wait /min test\singletest %count%
if %count% NEQ %last% start /min test\singletest %count%
set /a count=%count%+1
if %count% LSS %total% goto :begin
echo Testing complete.
echo Merging log files.. please wait.
rm test\multi.log test\header.log test\footer.log test\temp1.log test\temp2.log >nul 2>nul
echo. >test\temp1.log
echo. >test\temp2.log
set count=0
:mergestart
echo -----Test %count% start------ >test\header.log
echo -----Test %count% end------ >test\footer.log
echo. >>test\footer.log
copy test\temp1.log+test\header.log+test\test%count%.log+test\footer.log test\temp2.log >nul 2>nul
copy test\temp2.log test\temp1.log >nul 2>nul
rm test\test%count%.log >nul 2>nul
set /a count=%count%+1
if %count% LSS %total% goto :mergestart
copy test\temp1.log .\multitest.log >nul 2>nul
echo Cleaning up..
rm test/*.*
rmdir test
echo Complete. Output is in multitest.log
