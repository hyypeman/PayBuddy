# About EMVSync
EMVSync is a Point of Sale (POS) app which demonstrates the use of EMV and ISO 8583 to enable payments using the Nexgo Android SDK.
Work in Progress 🚧🚧

## Features
* Check card balance
* Card payment
* Automatic payment reversal for failed transactions
* Receipt printing


## Build Tools
* Kotlin
* Kotlin Coroutines for network calls and other intensive operations
* Kotlin Flows for view-viewModel interactions
* Navigation Component for Navigation
* Dagger-Hilt for Dependency Injection
* Nexgo Android SDK to interface with the POS terminal kernel and for EMV operations
* JPOS for ISO operations
* ISO 8583 library for constructing and deconstructing ISO message sent to NIBSS
* Sockets for network operations
* OkHttp to assist with socket response parsing
* Bouncy Castle for cryptographic operations
* Timber for logging
* Shared preferences for storing simple data types


## Screenshots
### Home Screen
<img src="https://github.com/judahben149/EMVSync/assets/71103838/32720282-c5fa-4d60-ae9e-46b8eff6515c" alt="Home screen" width="200">

### Card Balance Flow
<img src="https://github.com/judahben149/EMVSync/assets/71103838/f4365999-7e10-42f1-9a6b-72a86143c44f" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/400ca9e2-889f-49bd-88dc-163795485687" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/6c552156-2e29-411e-86a8-7c4467c63464" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/3db560fd-4945-4aa0-a265-b8df6a898775" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/2b6ae111-b80b-4c52-ada9-0ceb86586a95" alt="Home screen" width="200">


### Card Payment Flow
<img src="https://github.com/judahben149/EMVSync/assets/71103838/5ff4ccb1-a942-48d4-943e-0db917afaf9e" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/f4365999-7e10-42f1-9a6b-72a86143c44f" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/400ca9e2-889f-49bd-88dc-163795485687" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/6c552156-2e29-411e-86a8-7c4467c63464" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/56fbf3ec-07cc-4984-bc93-6842f1d5b2b2" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/d793afca-17d7-4350-8f53-3c896a938c81" alt="Home screen" width="200">



### Key Exchange Flow
<img src="https://github.com/judahben149/EMVSync/assets/71103838/172cefd2-487f-4a3e-bca5-f94fab6cd7ce" alt="Key Exchange Screen - Starting" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/a5af31ae-cf25-468e-b165-5b99e42195fd" alt="Key Exchange Screen - Midway" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/4bf971a1-99f6-4478-a7c0-1eaed1d3cc96" alt="Key Exchange Screen - Completed" width="200">

### Extras
<img src="https://github.com/judahben149/EMVSync/assets/71103838/31b64c7f-0ca2-4441-91d1-36f891db5bea" alt="Settings Screen - Completed" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/523a03a5-a023-49a2-aa07-c0528e3a9d9b" alt="Settings Screen - Completed" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/1b796845-ea39-4e4a-b7ff-4c074b61ab0b" alt="Settings Screen - Completed" width="200">\


## Clips
#### Card Balance Flow
https://github.com/judahben149/EMVSync/assets/71103838/136166ff-b6d6-46ff-a76a-8096e81947d5

#### Card Purchase Flow
https://github.com/judahben149/EMVSync/assets/71103838/fcdbbd72-8f79-4f69-a0d9-49855a6d51bf
##### Failed case
https://github.com/judahben149/EMVSync/assets/71103838/9e252199-3031-44a0-b715-4fb9128ecfae


