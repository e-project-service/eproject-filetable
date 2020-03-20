psexec -i -s cmd /k " net use m: \\db.xy.com\MSSQLSERVER2012FS /persistent:yes"
psexec -i -s cmd /k " net use n: \\db.xy.com\MSSQLSERVER2012FS\eProjectFileTable_fs_dir /persistent:yes"
psexec -i -s cmd /k " net use x: \\db.xy.com\MSSQLSERVER2012FS\eProjectFileTable_fs_dir\test_filetable /persistent:yes"
