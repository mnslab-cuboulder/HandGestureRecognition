# Wristband Serial Control Protocol
Controlling the wristband from a remote device is currently accomplished via a wired serial connection. The connection is set to run at 9600 baud, 8 data bits, 1 stop bit, and no parity.

# Commands
Commands from the host device to the wristband consist of a single byte to indicate the command. This is followed by zero or more bytes of parameter data.

## Echo 
The echo functionality allows the host device to determine if the wristband is alive and ready to communicate.

#### Request
`0xE0`

#### Response
`0xE0`

## Update Baseline
Update baseline instructs the wristband to take new baseline measurements of all capacitive sensors in use. The count parameter indicates how many measurements to use, 100 is typical.

This request is blocking. No other commands will be processed until baselining is complete.

#### Request
````
0xE1
count high byte
count low byte
````

#### Response
Once baselining is complete the device will respond.
`0xE1`

## Train
Training allows the host device to collect data for a specific gesture. The count parameter indicates how many measurements to collect. When this command is received the wristband will flash a light to inform the user collection is about to begin. The user should perform the gesture and press a button to begin training.

This request is blocking. No other commands will be processed until training is complete.

#### Request
````
0xE2
count high byte
count low byte
````

#### Response
The device will respond with `count` number of data frames. See data frame format below.

## Send Measurements
Once training is complete, normal operation will begin when a send measurements command is received.

#### Request
`0xE3`

#### Response
The device will send data frames indefinitely. See data frame format below.

## Stop Measurements
This command is used to halt measurements. Device is placed in an idle state.

#### Request
`0xE4`

#### Response
`0xE4`

# Data Frame Format
A data frame for capacitive measurements follows the protocol for the TI TouchPro Tool. Each data frame includes the following

1. Data head: `0x55AA`
1. Data length (1 byte)
1. For each sensor:
  - Sensor number (1 byte)
  - Sensor reading (2 bytes)
1. Checksum

The checksum is the sum of all bytes sent, dropping all rollover.

### Example
Suppose there is one sensor with a value of 1000 (0x03E8). The corresponding data frame byte stream is:

1. `0x55`
1. `0xAA`
1. `0x04`
1. `0x01`
1. `0x03`
1. `0xE8`
1. `0xEB`

The sum of the first five bytes is `0x01EB`. For the checksum we drop all rollover and use the low byte resulting in `0xEB`.