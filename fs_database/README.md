# Setup

- Enable FileStream
- Create Database
- Create FileTable
- Mapping FileTable



## Enable FileStream

1. SQL Server Configuration Manager
2. SQL Server Services
3. FILESTREAM tab
4. [Enable FILESTREAM for Transact-SQL access] and [Enable FILESTREAM for file I/O streaming access]
5. Run [config_filestream_ccess_level.sql](./init_database/config_filestream_ccess_level.sql)
6. Reboot SQL Server Services


## Create Database

Run [create_database.sql](./init_database/create_database.sql)

Then you can show check database filestream option use
[show_filestream_directory_name_of_all_database.sql](./init_database/show_filestream_directory_name_of_all_database.sql)
[show_transacted_access_of_all_databases.sql](./init_database/show_transacted_access_of_all_databases.sql)


## Create FileTable

_Scripts for testing are provided here. 
These scripts are not used in the production environment because the create table is automatic when startup application._

Run [create_table.sql](./init_database/create_table.sql)

Copy ALL [files](./files) to your FileStream Database Table share directory


## Mapping FileTable

Mapping FileTable network drive to local get the feature of local file system.

_Scripts for testing are provided here. 
These scripts are not used in the production environment because the mapping is automatic when startup application._

| script | mapping/unmapping | remote SqlServer | local drive | when use |
|-----|-----|-----|-----|-----|
| [mapping_n_with_local_by_psexec.bat](init_mapping/mapping_n_with_local_by_psexec.bat) | Mapping | 127.0.0.1 | n | database and application in one machine | 
| [mapping_n_with_db_by_psexec.bat](init_mapping/mapping_n_with_db_by_psexec.bat) | Mapping | db.xy.com | n | database on db.xy.com |
| [mapping_n_with_me_by_psexec.bat](init_mapping/mapping_n_with_me_by_psexec.bat) | Mapping | me | n | database and application in one machine and use specified network interface |
| [unmapping_n_by_psexec.bat](init_mapping/unmapping_n_by_psexec.bat) | UnMapping | | n |  | 
| [mapping_n_with_local_by_user.bat](init_mapping/mapping_n_with_local_by_user.bat) | Mapping | 127.0.0.1 | n | database and application in one machine | 
| [mapping_n_with_db_by_user.bat](init_mapping/mapping_n_with_db_by_user.bat) | Mapping | db.xy.com | n | database on db.xy.com |
| [mapping_n_with_me_by_user.bat](init_mapping/mapping_n_with_me_by_user.bat) | Mapping | me | n | database and application in one machine and use specified network interface |
| [unmapping_n_by_user.bat](init_mapping/unmapping_n_by_user.bat) | UnMapping | | n | |
| [mapping_xmn_with_db_by_user.bat](init_mapping/mapping_xmn_with_db_by_user.bat) | Mapping | db.xy.com | x,m,n | for UnitTest database on db.xy.com |
| [unmapping_xmn_by_user.bat](init_mapping/unmapping_xmn_by_user.bat) | UnMapping | | x,m,n | for UnitTest database on db.xy.com |


**NOTE**: If you want to separate the SqlServer database from the eProject-FileTable application, 
you can add domain `db.xy.com` or `me` in HOSTS to point to the SqlServer database address,
and use corresponding script.

#### Mapping remote SMB to localhost for unit test

for unit test you must map FileTable 'test_filetable' to 'X:\' drive
 
refer this 
[mapping_xmn_with_db_by_user.bat](init_mapping/mapping_xmn_with_db_by_user.bat)
[unmapping_xmn_by_user.bat](init_mapping/unmapping_xmn_by_user.bat)

Then modify configurations in [resources](./../core/src/test/resources)


#### Mapping remote SMB to localhost as service

mapping as service run with SYSTEM account, 
you need [SysinternalsSuite](https://docs.microsoft.com/en-us/sysinternals/downloads/sysinternals-suite) `psexec`
run `*_psexec.bat` mapping commands.

See [map-a-network-drive-to-be-used-by-a-service](https://stackoverflow.com/questions/182750/map-a-network-drive-to-be-used-by-a-service)


