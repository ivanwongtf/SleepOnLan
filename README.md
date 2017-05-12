# SleepOnLan
A software performs preseted commands when receiving correct WOL packets.

## Specification
Only tested on Windows 7 and 10. Not sure whether this software works on Linux.

## How to sleep remotely
SleepOnLan will only run commands that you have set before, therefore, in order to really sleep the computer remotely, you will need software such as PsShutdown.

Steps (Windows):
1. Download PsShutdown: [https://technet.microsoft.com/en-us/sysinternals/psshutdown.aspx](https://technet.microsoft.com/en-us/sysinternals/psshutdown.aspx)

2. Go to `Settings -> Command Settings`, at here, you enter the command by using PsShutdown. Example:
> C:\SolServer\psshutdown.exe -d -c -t 10
(-t 10 means sleep after 10 seconds)

3. You may also choose to call the command when receiving Normal MAC of your computer or the Reverse one (Default is Reversed MAC)

## Settings
### Server Settings
* Port to listen: decide which port should SleepOnLan listen to.
* Buffer size: string buffer size for Magic Packet, the unit here is bytes, usually the size of Magic Packet is 144.
* Start listening when open: default is Yes.
* Network Interface: decide which network interface should SleepOnLan listen to.

### Command Settings
* Call at detecting: choose normal MAC or Reversed MAC as the 'Key' to trigger the command.
* Command: command to be triggered when receiving correct MAC.
