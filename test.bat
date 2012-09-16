@echo off
set count=0
set total=10
:begin
start loadtest
set /a count=%count%+1
if %count% LSS %total% goto :begin