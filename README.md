# Nutrition Scanner

You scan a food barcode (or type it in if you don’t have a camera handy), and it pulls up basic product info and gives a nutrition score. This app is more of a proof-of-concept (or demo) that could be turned into a full app.

## What does it do?

- Opens on a scanner screen: use the camera to read a barcode, or enter the digits manually.
- Sends that barcode to **Open Food Facts** and shows whatever comes back: name, brand, quantity, categories, ingredients when available, nutrition grade, calories when the product has them, and a product photo when the image URL loads.

## Data Source

All product data comes from **[Open Food Facts](https://world.openfoodfacts.org/)**, which is a free, crowdsourced food database anyone can contribute to. The app hits their public JSON API.

Handy Links:

- Website: https://openfoodfacts.org/
- API / data overview: https://world.openfoodfacts.org/data
- Main open-source codebase (server + API): https://github.com/openfoodfacts/openfoodfacts-server

Feel free to donate and contribute by visiting their website!

## Tech Stack

Kotlin, XML layouts, two fragments, Navigation Component, ViewModel + LiveData, Retrofit, Glide for images, ZXing embedded for scanning.

## How do I run it?
*This quick-guide assumes you have **Android Studio** already installed on your system.*

1. Open the project folder in Android Studio.
2. If you didn't already, sync Gradle.
3. Press "Build" to run on a device or emulator.
4. The app should open automatically and you can mess around with it!

*You’ll need network access for API calls (and camera permission if you use the scanner).*