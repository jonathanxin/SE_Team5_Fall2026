# SE_Team5_Fall2026

## Team Members

| GitHub Name | Real Name |
|-------------|-----------|
| Vonising | Bradley Ising |
| michaelchen298 | Michael Chen |
| jonathanxin | Jonathan Xin |
| IsaacTempest | Isaac Berry |
| SamekhKusma | Samekh Kusma |

# Supported Environment

This project is intended to run inside the course Linux virtual machine.

The setup supports Linux VMs using:

x86_64 / AMD64

ARM64 / aarch64

This allows the project to run in the Linux VM whether the host computer is Windows, Intel/AMD, or an Apple Silicon Mac.

# Requirements

The install script automatically sets up the required project dependencies, including:

JDK 21

JavaFX 21

PostgreSQL

PostgreSQL JDBC driver

photon database

players table

JDK 21 is installed locally inside the project so the application uses the same Java version regardless of the Java version already installed on the VM.

The install script also detects the VM architecture and downloads the appropriate JavaFX files for x86_64 or ARM64 systems.

# Installation

Clone the repository:

git clone https://github.com/jonathanxin/SE_Team5_Fall2026.git

Enter the project directory:

cd SE_Team5_Fall2026

Make the scripts executable:

chmod +x install.sh
chmod +x build.bash
chmod +x setup_database.sh

Run the install script:

./install.sh

The install script will:

Detect the Linux VM architecture.

Install the required setup utilities.

Download and configure JDK 21 for the project.

Download the correct JavaFX 21 files for the VM architecture.

Download the PostgreSQL JDBC driver.

Install and configure PostgreSQL if needed.

Create the photon database and players table if needed.

# Run the Application

After installation is complete, run:

./build.bash

The build script uses the JDK and JavaFX versions installed by the project.

If the required project dependencies have not been installed, the build script will display an error and instruct you to run:

./install.sh

# Player Entry Controls

Enter a Player ID and press Tab.

Follow the on-screen prompts to enter a nickname for a new player and an equipment ID.

Press F12 to clear all player entries.

