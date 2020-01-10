CREATE DATABASE eProjectFileTable
ON
PRIMARY ( 
  NAME = eProjectFileTable,
  FILENAME = 'D:\DATA\eProjectFileTable.mdf'),
FILEGROUP DefaultFileStreamGroup CONTAINS FILESTREAM(
  NAME = eProjectFileTable_fs,
  FILENAME = 'D:\DATA\eProjectFileTableFileStream')
LOG ON  ( 
  NAME = eProjectFileTable_log,
  FILENAME = 'D:\DATA\eProjectFileTable.ldf')

WITH FILESTREAM ( NON_TRANSACTED_ACCESS = FULL, DIRECTORY_NAME = N'eProjectFileTable_fs_dir' )
GO