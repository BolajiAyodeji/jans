import sys
import argparse
import pymysql
import psycopg2
import psycopg2.extras

parser = argparse.ArgumentParser(description="This script generates rdbm schema doc for Jansssen RDBM backend")
parser.add_argument('-hostname', help="Hostname or IP address of server", default='localhost')
parser.add_argument('-username', help="RDBM username", default='jans')
parser.add_argument('-password', help="Password for RDBM user")
parser.add_argument('-database', help="RDBM database name", default='jansdb')
parser.add_argument('-schema-file', help="Path of schema file")
parser.add_argument('-schema-indexes-file', help="Path of schema indexes file")
parser.add_argument('-rdbm-type', help="Type of RDBM", choices=['mysql', 'pgsql'], default='mysql')
argsp = parser.parse_args()



class RDBMSchemaGenerator:

    def __init__(self):
        if argsp.rdbm_type == 'mysql':
            self.table_titles = ('Field', 'Type', 'Null', 'Key', 'Default', 'Comment')
            self.index_titles = ('Table', 'Non_unique', 'Key_name', 'Seq_in_index', 'Column_name', 'Null', 'Comment', 'Index_comment')
        else:
            self.table_titles = ('Field', 'Type', 'Character Maximum Length', 'Null', 'Default', 'Comment')
            self.index_titles = ('tablename', 'indexname', 'indexdef')

        self.schema_file = argsp.schema_file or argsp.rdbm_type + '-schema.md'
        self.schema_indexes_file = argsp.schema_indexes_file or argsp.rdbm_type + '-schema-indexes.md'

    def connect(self):
        getattr(self, 'connect_to_' + argsp.rdbm_type)()

    def connect_to_mysql(self):
        self.connection = pymysql.connect(host=argsp.hostname,
                                          user=argsp.username,
                                          password=argsp.password,
                                          database=argsp.database,
                                          charset='utf8')
        self.cursor = self.connection.cursor()

    def connect_to_pgsql(self):
        self.connection = psycopg2.connect(host=argsp.hostname,
                                    user=argsp.username,
                                    password=argsp.password,
                                    dbname= argsp.database)
        self.cursor = self.connection.cursor()


    def get_tables(self):
        if argsp.rdbm_type == 'mysql':
            cmd = "SHOW tables FROM {}".format(argsp.database)
        else:
            cmd = "SELECT table_name FROM information_schema.tables where table_schema='public' AND  table_catalog='{}' AND table_type='BASE TABLE';".format(argsp.database)

        self.cursor.execute(cmd)
        tables = self.cursor.fetchall()
        self.tables = [table[0] for table in tables]

    def get_dict_cursor_object(self):
        return self.connection.cursor(pymysql.cursors.DictCursor) if argsp.rdbm_type == 'mysql' else self.connection.cursor(cursor_factory=psycopg2.extras.RealDictCursor)

    def get_table_fields(self, table):
        if argsp.rdbm_type == 'mysql':
            cmd = "SHOW FULL COLUMNS FROM {}".format(table)
        else:
            cmd = '''
                            select
                                c.column_name as "Field",
                                c.data_type as "Type",
                                c.character_maximum_length as "Character Maximum Length",
                                c.is_nullable as "Null",
                                c.column_default as "Default",
                                pgd.description as "Comment"
                            from pg_catalog.pg_statio_all_tables as st
                            inner join pg_catalog.pg_description pgd on (
                                pgd.objoid = st.relid
                            )
                            inner join information_schema.columns c on (
                                pgd.objsubid   = c.ordinal_position and
                                c.table_schema = st.schemaname and
                                c.table_name   = st.relname
                            ) where c.table_name='{}';
                            '''.format(table)

        with self.get_dict_cursor_object() as cursor:
            cursor.execute(cmd)
            fields = cursor.fetchall()
            return fields

    def get_indexes(self, table):
        if argsp.rdbm_type == 'mysql':
            cmd = "SHOW INDEX FROM {}".format(table)
        else:
            cmd = "SELECT * FROM pg_indexes WHERE tablename = '{}'".format(table)

        with self.get_dict_cursor_object() as cursor:
            cursor.execute(cmd)
            indexes = cursor.fetchall()
            return indexes

    def get_list_from_dict(self, data_dict, key_list):
        return [str(data_dict.get(key, '')) for key in key_list]

    def print_tables(self):
        with open(self.schema_file, 'w') as w:
            for table in self.tables:
                w.write('\n\n**' + table + '**\n')
                w.write('|' + '|'.join(self.table_titles) + '|\n')
                w.write('|-'*len(self.table_titles)+'|\n')
                fields = self.get_table_fields(table)
                for field in fields:
                    w.write("|" + "|".join(self.get_list_from_dict(field, self.table_titles)) + '|\n')


    def print_indexes(self):
        with open(self.schema_indexes_file, 'w') as w:
            for table in self.tables:
                w.write("\n\n**" + table + "**\n")
                w.write('|' + '|'.join(self.index_titles) + '|\n')
                w.write('|-'*len(self.index_titles)+'|\n')
                indexes = self.get_indexes(table)
                for index in indexes:
                    w.write("|" + "|".join(self.get_list_from_dict(index, self.index_titles)) + '|\n')

def main():
    schema_generator = RDBMSchemaGenerator()
    schema_generator.connect()
    schema_generator.get_tables()
    schema_generator.print_tables()
    schema_generator.print_indexes()

if __name__ == '__main__':
    main()
