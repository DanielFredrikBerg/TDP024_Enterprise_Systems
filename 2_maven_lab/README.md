# To-Do API

This is a backend for managing a simple task management application.
The backend consists of three different parts:

- Go application
- SQLite (default)
- PostgreSQL (optional)
- NGINX (optional)

An overview of the system can be seen here (dotted boxes indicates optional parts):

![](architecture.png)

## Go application

In order to run the application, you need one dependency: "github.com/lib/pq","github.com/mattn/go-sqlite3".
This can be retrieved by running `go get`.

If you are using SQLite you may want to change the database file. 
Update the following line in todo.go:
sql.Open("sqlite3", "./foo.db")

If you are using PostgreSQL you may want to update the database information to match your system.
Update the following constants in todo.go:

- databaseUser
- databaseHost
- databaseName

## SQLite database 

If SQLite is used to store all the list and todo information.
In order to setup the database schema, run `sqlite3  foo.db < schema.sql`.

## PostgreSQL database

If PostgreSQL is used to store all the list and todo information.
In order to setup the database schema, run `psql -f schema.sql db_name`.

## NGINX (Optional)

There is a configuration ready for using NGINX as a reverse proxy (in `nginx.conf`).
However, the url to the application may need to be updated.

## Run

To build to application, simply use `go build` in the project directory.
You can also use `go run todo.go` to run the server directly.

## Test

Test the system to make sure everything works.
This is done by running `go test` in the application folder.
