# About EMVSync
EMVSync is a demo app which demonstrates the use of EMV and ISO 8583 to enable POS payments using the Nexgo Android SDK.
Work in Progress 🚧🚧

## Features
* Card balance checking
* Card payment functionality
* Automatic reversal of failed transanctions


## Build Tools
* Kotlin
* Kotlin Coroutines
* Navigation Component for Navigation
* Dagger-Hilt for Dependency Injection
* Nexgo Android SDK to interface with the POS terminal device and for EMV operations
* JPOS for ISO operations
* ISO 8583 library for constructing and deconstructing ISO message sent to NIBSS
* Sockets for network operations
* OkHttp to assist with socket response parsing
* Bouncy Castle for cryptography
* Timber for logging
* Shared preferences


## App Flow demo


## Screenshots
### Home Screen
<img src="https://github.com/judahben149/EMVSync/assets/71103838/32720282-c5fa-4d60-ae9e-46b8eff6515c" alt="Home screen" width="200">

### Card Balance Flow
<img src="https://github.com/judahben149/EMVSync/assets/71103838/f4365999-7e10-42f1-9a6b-72a86143c44f" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/400ca9e2-889f-49bd-88dc-163795485687" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/6c552156-2e29-411e-86a8-7c4467c63464" alt="Home screen" width="200">

### Card Payment Flow
<img src="https://github.com/judahben149/EMVSync/assets/71103838/5ff4ccb1-a942-48d4-943e-0db917afaf9e" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/f4365999-7e10-42f1-9a6b-72a86143c44f" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/400ca9e2-889f-49bd-88dc-163795485687" alt="Home screen" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/6c552156-2e29-411e-86a8-7c4467c63464" alt="Home screen" width="200">



### Key Exchange Flow
<img src="https://github.com/judahben149/EMVSync/assets/71103838/172cefd2-487f-4a3e-bca5-f94fab6cd7ce" alt="Key Exchange Screen - Starting" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/a5af31ae-cf25-468e-b165-5b99e42195fd" alt="Key Exchange Screen - Midway" width="200">
<img src="https://github.com/judahben149/EMVSync/assets/71103838/4bf971a1-99f6-4478-a7c0-1eaed1d3cc96" alt="Key Exchange Screen - Completed" width="200">

### Settings Screen
<img src="https://github.com/judahben149/EMVSync/assets/71103838/31b64c7f-0ca2-4441-91d1-36f891db5bea" alt="Settings Screen - Completed" width="200">



## Clips
#### Card Balance Flow
https://github.com/judahben149/EMVSync/assets/71103838/136166ff-b6d6-46ff-a76a-8096e81947d5

#### Card Purchase Flow
https://github.com/judahben149/EMVSync/assets/71103838/fcdbbd72-8f79-4f69-a0d9-49855a6d51bf
##### Failed case
https://github.com/judahben149/EMVSync/assets/71103838/9e252199-3031-44a0-b715-4fb9128ecfae


