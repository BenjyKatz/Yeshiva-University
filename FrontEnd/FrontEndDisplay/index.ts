import axios from 'axios';
import { getMapArr } from './mapData'; // Import the mapData module

let mapArray: google.maps.Polygon[] = [];
const zoneInfoArray: MapInfo []=[];
let map: google.maps.Map;
let myColor: Color;


class MapInfo {
    // Properties
    private averageTotalAmount: number;
    private count: number;
    private averageDuration: number;
    private averageTripDistance: number;
    private heuristic: number;
  
    // Constructor
    constructor(averageTotalAmount: number, count: number, averageDuration: number, averageTripDistance: number, heuristic: number) {
      this.averageTotalAmount = averageTotalAmount;
      this.count = count;
      this.averageDuration = averageDuration;
      this.averageTripDistance = averageTripDistance;
      this.heuristic = heuristic;
    }
    getAverageTotalAmount(): number {
        return this.averageTotalAmount;
      }
  
    getCount(): number {
        return this.count;
    }

    getAverageDuration(): number {
        return this.averageDuration;
    }

    getAverageTripDistance(): number {
        return this.averageTripDistance;
    }
    getHeuristic(): number {
        return this.heuristic;
    }
  }

function initMap(): void {
  map = new google.maps.Map(document.getElementById("map") as HTMLElement, {
    zoom: 10.5,
    center: { lat: 40.74000, lng: -73.958722 }
  });

  
  
    // Set CSS styles for the info window content
    const infoWindowContentStyle = `
        #infoContent {
            font-size: 32px;
            padding: 5px;
        }
    `;

    

    for (let i=0; i<263; i++) {
        zoneInfoArray.push(new MapInfo(0, 0, 0, 0, 0));
    }

    mapArray = getMapArr();
    
    let currentInfoWindow: google.maps.InfoWindow | null = null;

    for (let i = 0; i < mapArray.length; i++) {
        const polygon = mapArray[i];
        polygon.setMap(map);
        const infoWindow = new google.maps.InfoWindow({
            content: "<div id='infoContent'>Zone Number: " + polygon.get("zIndex") + "\n</div>",
        });
        infoWindow.setContent(`<style>${infoWindowContentStyle}</style>${infoWindow.getContent()}`);
        
        // Function to display info window when polygon is clicked
        function displayInfoWindow(event: google.maps.KmlMouseEvent) {
            if (currentInfoWindow) {
                currentInfoWindow.close();
            }
            const zoneIndex = polygon.get("zIndex") as number;
            const mapInfo = zoneInfoArray[zoneIndex - 1]; // Get the MapInfo for previous index
            const content = `
            <div id='infoContent'>
                Zone Number: ${zoneIndex} <br>
                Average Total Amount: $${Number(mapInfo.getAverageTotalAmount()).toFixed(2)} <br>
                Number of Taxis: ${Number(mapInfo.getCount())} <br>
                Average Duration: ${Number(mapInfo.getAverageDuration()).toFixed(2)} minutes <br>
                Average Trip Distance: ${Number(mapInfo.getAverageTripDistance()).toFixed(2)} miles <br>
            </div>`;
            infoWindow.setContent(`<style>${infoWindowContentStyle}</style>${content}`);
            infoWindow.setPosition(event.latLng);
            infoWindow.open(map);
            currentInfoWindow = infoWindow;
        }
        
        // Add the click listener
        google.maps.event.addListener(polygon, 'click', displayInfoWindow);
    }

    myColor = Color.Price;
}

// Call initMap when the page loads
window.addEventListener("load", () => {
  initMap();
});

enum Color {
    Price,
    Duration,
    Distance,
    Number,
    Heuristic,
    ML
}

function getCurrentDateTime(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }

async function updateInfo() {
    const datetime = getCurrentDateTime();
    const url = `http://localhost:5000/historical?datetime=${datetime}`;

    try {
        const totalResponse = await fetch(url);
        const response = await totalResponse.json();
        parseResult(response);
        updateColors();
    } catch (error) {
        console.error('Error:', error);
    }
    console.log(zoneInfoArray);

}

updateInfo();

setInterval(updateInfo, 30 * 1000);

function parseResult(response: any): void {
    console.log(response);
    let maxZone = -1;
    let maxDuration = -1;
    for (let i=1; i<=263; i++) {
        const infoI = response[i]; // Access the property directly from the object
        if (!infoI) {
            console.log('Error ' + i);
        }
        else {
            let duration = parseInt(infoI["heuristic"]);
            if (duration>maxDuration) {
                maxZone = i;
                maxDuration = duration;
            }
            zoneInfoArray[i-1] = new MapInfo(infoI["AVG_total_amount"], infoI["COUNT"], infoI["avg_duration"], infoI["AVG_trip_distance"], infoI["heuristic"]); // Access the properties using square brackets
        }
    }
    console.log("Max zone is " + maxZone + " with value " + maxDuration);
}


function numberToColor(value: number): string {
    // Clamp the value between 0 and 1
    const clampedValue = Math.min(1, Math.max(0, value));

    // Define RGB values for red, yellow, and green
    const red = [255, 0, 0];
    const yellow = [255, 255, 0];
    const green = [0, 255, 0];

    let color: number[];

    // Determine the color based on the value
    if (clampedValue <= 0.5) {
        // Interpolate between red and yellow
        const ratio = clampedValue / 0.5;
        color = red.map((channel, index) =>
            Math.round(channel + ratio * (yellow[index] - channel))
        );
    } else {
        // Interpolate between yellow and green
        const ratio = (clampedValue - 0.5) / 0.5;
        color = yellow.map((channel, index) =>
            Math.round(channel + ratio * (green[index] - channel))
        );
    }

    // Convert RGB values to hexadecimal color code
    const hexColor = color.reduce((acc, channel) => {
        const hex = channel.toString(16).padStart(2, '0');
        return acc + hex;
    }, '#');

    return hexColor;
}

//price - max 142
//duration - max 73
//distance - max 21
//number of taxis - max 438
//heuristic - max 434
function updateColors() {
    for (let i = 0; i < mapArray.length; i++) {
        const polygon = mapArray[i];
        const zoneIndex = polygon.get("zIndex") as number;
        let number;
        if (myColor==Color.Distance) {
            number = Math.log(zoneInfoArray[zoneIndex-1].getAverageTripDistance())/3.05;
        } else if (myColor==Color.Duration) {
            number = Math.log(zoneInfoArray[zoneIndex-1].getAverageDuration())/4.3;
        } else if (myColor==Color.Number) {
            number = Math.log(zoneInfoArray[zoneIndex-1].getCount())/6.1;
        } else if (myColor==Color.Price) {
            number = Math.log(zoneInfoArray[zoneIndex-1].getAverageTotalAmount())/5;
        } else if (myColor==Color.Heuristic) {
            number = Math.log(zoneInfoArray[zoneIndex-1].getHeuristic())/6.1;
        }
        polygon.setOptions({fillColor: numberToColor(number), strokeColor: numberToColor(number)});
        polygon.setMap(null);
        polygon.setMap(map);
    }
}


// Define a function to handle radio button changes
function handleRadioButtonChange(event: Event) {
    const selectedOption = (event.target as HTMLInputElement).value;
    // Perform actions based on the selected option
    if (selectedOption=="price") {
        myColor = Color.Price;
    } else if (selectedOption=="duration") {
        myColor = Color.Duration;
    } else if (selectedOption=="taxis") {
        myColor = Color.Number;
    } else if (selectedOption=="heuristic") {
        myColor = Color.Heuristic;
    } else if (selectedOption=="model") {
        myColor = Color.ML;
    }
    updateColors();
  }
  
  // Get a reference to the radio buttons
  const radioButtons = document.querySelectorAll<HTMLInputElement>('input[name="color-option"]');
  
  // Attach event listeners to the radio buttons
  radioButtons.forEach(button => {
    button.addEventListener('change', handleRadioButtonChange);
  });