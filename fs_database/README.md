# Setup

- Enable SQLServer FileStream support
- Create Database
- Create FileTable
- Mapping remote SMB to localhost for unit test



## Enable FileStream

1. SQL Server Configuration Manager
2. SQL Server Services
3. FILESTREAM tabe
4. [Enable FILESTREAM for Transact-SQL access] and [Enable FILESTREAM for file I/O streaming access]
5. Run [config_filestream_ccess_level.sql](./init_database/config_filestream_ccess_level.sql)
6. Reboot SQL Server Services


## Create Database
Run [create_database.sql](./init_database/create_database.sql)

Then you can show check database filestream option use
[show_filestream_directory_name_of_all_database.sql](./init_database/show_filestream_directory_name_of_all_database.sql)
[show_transacted_access_of_all_databases.sql](./init_database/show_transacted_access_of_all_databases.sql)


## Create FileTable
Run [create_table.sql](./init_database/create_table.sql)

Copy ALL [files](./files) to your FileStream Database Table share directory


## Mapping remote SMB to localhost for unit test

See [map-a-network-drive-to-be-used-by-a-service](https://stackoverflow.com/questions/182750/map-a-network-drive-to-be-used-by-a-service)
map FileTable 'test_filetable' to 'X:\' drive
 
refer this 
[mapping.sql](./init_mapping/mapping.bat)
[unmapping.sql](./init_mapping/unmapping.bat)

Then modify configurations in [resources](./../core/src/test/resources)