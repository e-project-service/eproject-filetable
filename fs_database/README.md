

# Enable FileStream

1. SQL Server Configuration Manager
2. SQL Server Services
3. FILESTREAM tabe
4. [Enable FILESTREAM for Transact-SQL access] and [Enable FILESTREAM for file I/O streaming access]
5. Run [config_filestream_ccess_level.sql](./init_database/config_filestream_ccess_level.sql)
6. Reboot SQL Server Services


# Create Database
Run [create_database.sql](./init_database/create_database.sql)

Then you can show check database filestream option use
[show_filestream_directory_name_of_all_database.sql](./init_database/show_filestream_directory_name_of_all_database.sql)
[show_transacted_access_of_all_databases.sql](./init_database/show_transacted_access_of_all_databases.sql)


# Create FileTable
Run [create_table.sql](./init_database/create_table.sql)

Copy ALL [files](./files) to your FileStream Database Table share directory