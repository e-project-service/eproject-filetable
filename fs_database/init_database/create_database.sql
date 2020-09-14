CREATE DATABASE eProjectFileTable
ON
PRIMARY (
  NAME = eProjectFileTable_db,
  FILENAME = 'D:\Data\eProjectFileTable.mdf'),
FILEGROUP DefaultFileStreamGroup CONTAINS FILESTREAM(
  NAME = eProjectFileTable_fs,
  FILENAME = 'D:\Data\eProjectFileTable')
LOG ON  (
  NAME = eProjectFileTable_log,
  FILENAME = 'D:\Data\eProjectFileTable.ldf')

WITH FILESTREAM ( NON_TRANSACTED_ACCESS = FULL, DIRECTORY_NAME = N'eProjectFileTable_fs_dir' )
GO