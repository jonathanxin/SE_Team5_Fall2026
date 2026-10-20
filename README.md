# SE_Team5_Fall2026

## Team Members

| GitHub Name | Real Name |
|-------------|-----------|
| Vonising | Bradley Ising |
| michaelchen298 | Michael Chen |
| jonathanxin | Jonathan Xin |
| IsaacTempest | Isaac Berry |
| SamekhKusma | Samekh Kusma |

## Requirements

The project is designed to run on Linux.

The install script handles the required setup, including:

- Java 17 or newer
- JavaFX 17
- PostgreSQL
- PostgreSQL JDBC driver
- photon database
- players table

JavaFX dependencies are selected automatically for ARM64 or x86_64 Linux systems.

## Installation

Clone the repository:

git clone https://github.com/jonathanxin/SE_Team5_Fall2026.git

Enter the project directory:

cd SE_Team5_Fall2026

Make the install script executable and run it:

chmod +x install.sh
./install.sh

The install script downloads the required dependencies and sets up the PostgreSQL database.

## Run the Application

Make the build script executable:

chmod +x build.bash

Run the application:

./build.bash

## Player Entry Controls

Enter a Player ID and press Tab.

Follow the on-screen prompts to enter a nickname for a new player and an equipment ID.

Press F12 to clear all player entries.
