-- Create the second database
CREATE DATABASE evalify;

-- Create a new user for the second database
CREATE USER evalify_admin WITH PASSWORD 'evalify';

-- Grant privileges to the new user on the new database
GRANT ALL PRIVILEGES ON DATABASE evalify TO evalify_admin;

ALTER DATABASE evalify OWNER TO evalify_admin;