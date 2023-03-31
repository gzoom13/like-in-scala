# How to start

To run start PostgreSQL container first

```bash
docker run --name api-server-scala-example -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```

Then populate it with data

```sql
CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);
insert INTO users values('smaldini', 'Stéphane', 'Maldini'),('sdeleuze', 'Sébastien', 'Deleuze'),('bclozel', 'Brian', 'Clozel');
```