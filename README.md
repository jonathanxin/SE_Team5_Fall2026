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

The install script sets up these project dependencies:

- JDK 21
- JavaFX 21
- PostgreSQL JDBC driver

The application uses the `photon` database already provided in the course VM.
The install script does not configure PostgreSQL or modify the database.

JDK 21 is installed locally inside the project so the application uses the same Java version regardless of the Java version already installed on the VM.

The install script also detects the VM architecture and downloads the appropriate JavaFX files for x86_64 or ARM64 systems.

The course Linux VM must have internet access and sudo access.

No separate Java or JavaFX installation is required. The install script downloads JDK 21 and JavaFX 21 for the project automatically.

# Installation

Clone the repository:

git clone https://github.com/jonathanxin/SE_Team5_Fall2026.git

Enter the project directory:

cd SE_Team5_Fall2026

Make the scripts executable:

chmod +x install.sh
chmod +x build.bash

Run the install script:

./install.sh

The install script will:

Detect the Linux VM architecture.

Install the required setup utilities.

Download and configure JDK 21 for the project.

Download the correct JavaFX 21 files for the VM architecture.

Download the PostgreSQL JDBC driver.

# Run the Application

After installation is complete, run:

./build.bash

The build script uses the JDK and JavaFX versions installed by the project.

If the required project dependencies have not been installed, the build script will display an error and instruct you to run:

./install.sh

# Player Entry Controls

Players can be added to either team by clicking the next available player entry field under Green Team or Red Team. Players do not need to alternate between teams.

Enter a Player ID and press Tab.

Follow the on-screen prompts to enter a nickname for a new player and an equipment ID.

Press F5 to start the game countdown.

Press F12 to clear all player entries.

The default network address is 127.0.0.1. To use a different network address, enter it in the network field and click Change Network.



# Testing Gameplay

Launch Photon and enter two players on each team using their database player IDs. Assign the following equipment IDs:

| Team | Player | Equipment ID |
|---|---|---|
| Green | Player 1 | 11 |
| Green | Player 2 | 12 |
| Red | Player 1 | 21 |
| Red | Player 2 | 22 |

Open a second terminal and download the instructor's traffic generator:

*Python 3 is required to run the instructor's traffic generator, the generator is only needed for testing gameplay, not for launching Photon*

```bash
curl -fL https://raw.githubusercontent.com/jstrother123/photon-main/main/udp_files/python_trafficgenarator_v2.py -o /tmp/python_trafficgenarator_v2.py
```

Run the generator:

```bash
python3 /tmp/python_trafficgenarator_v2.py
```

When prompted, enter the equipment IDs in this order:

1. Red player 1: 21
2. Red player 2: 22
3. Green player 1: 11
4. Green player 2: 12

Wait until the generator displays "waiting for start from game_software", then return to Photon and press F5.

After the 30-second countdown, the generator sends gameplay events. Player scores and team totals update as hits are received, including friendly fire and base hits.

The game runs for six minutes and sends the stop code (221) when it ends.

