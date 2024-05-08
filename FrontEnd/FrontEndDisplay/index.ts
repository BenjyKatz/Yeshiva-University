import axios from 'axios';
import { getMapArr } from './mapData'; // Import the mapData module

let mapArray: google.maps.Polygon[] = [];
const historicalInfoArray: MapInfo []=[];
const predictiveInfoArray: MapInfo []=[];
let map: google.maps.Map;
let myColor: Color;
let myModel: Model;


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
        historicalInfoArray.push(new MapInfo(0, 0, 0, 0, 0));
        predictiveInfoArray.push(new MapInfo(0, 0, 0, 0, 0));
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
            let mapInfo : MapInfo;
            let content: string;
            if (myModel==Model.Historical) {
                mapInfo = historicalInfoArray[zoneIndex - 1]; // Get the MapInfo for previous index
                content = `
                <div id='infoContent'>
                    Zone Number: ${zoneIndex} <br>
                    Average Total Amount: $${Number(mapInfo.getAverageTotalAmount()).toFixed(2)} <br>
                    Number of Taxis: ${Number(mapInfo.getCount())} <br>
                    Average Duration: ${Number(mapInfo.getAverageDuration()).toFixed(2)} minutes <br>
                    Average Trip Distance: ${Number(mapInfo.getAverageTripDistance()).toFixed(2)} miles <br>
                </div>`;
            } else {
                mapInfo = predictiveInfoArray[zoneIndex - 1]; // Get the MapInfo for previous index
                content = `
                <div id='infoContent'>
                    Zone Number: ${zoneIndex} <br>
                    Average Total Amount: $${Number(mapInfo.getAverageTotalAmount()).toFixed(2)} <br>
                    Number of Taxis (Predicted): ${Number(mapInfo.getCount()).toFixed(2)} <br>
                    Average Duration: ${Number(mapInfo.getAverageDuration()).toFixed(2)} minutes <br>
                    Average Trip Distance: ${Number(mapInfo.getAverageTripDistance()).toFixed(2)} miles <br>
                </div>`;
            }
            
            infoWindow.setContent(`<style>${infoWindowContentStyle}</style>${content}`);
            infoWindow.setPosition(event.latLng);
            infoWindow.open(map);
            currentInfoWindow = infoWindow;
        }
        
        // Add the click listener
        google.maps.event.addListener(polygon, 'click', displayInfoWindow);
    }

    myColor = Color.Price;
    myModel = Model.Historical;
}

// Call initMap when the page loads
window.addEventListener("load", () => {
  initMap();
});

enum Model {
    Historical,
    Predictive
}
enum Color {
    Price,
    Duration,
    Distance,
    Number,
    Heuristic,
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
    const historicalURL = `http://127.0.0.1:5000/historical?datetime=${datetime}`;
    const predictiveURL = `http://127.0.0.1:5000/predictive?datetime=${datetime}`;
    

    try {
        const histTotalResponse = await fetch(historicalURL);
        const histResponse = await histTotalResponse.json();
        const predTotalResponse = await fetch(predictiveURL);
        const predResponse = await predTotalResponse.json();
        
        parseResult(histResponse, predResponse);
        updateColors();
    } catch (error) {
        console.error('Error:', error);
    }

}

updateInfo();

setInterval(updateInfo, 30 * 1000);

function parseResult(histResponse: any, predResponse: any): void {
    // let max = -1;
    // let maxZone = -1;
    for (let i=1; i<=263; i++) {
        const histInfoI = histResponse[i]; // Access the property directly from the object
        const predInfoI = predResponse[i]; // Access the property directly from the object
        
        if (histInfoI) {
            // let currentAmount = histInfoI["COUNT"];
            // if (currentAmount>max) {
            //     max = currentAmount;
            //     maxZone = i;
            // }
            historicalInfoArray[i-1] = new MapInfo(histInfoI["AVG_total_amount"], histInfoI["COUNT"], histInfoI["avg_duration"], histInfoI["AVG_trip_distance"], histInfoI["heuristic"]); // Access the properties using square brackets
        }
        if (predInfoI) {
            predictiveInfoArray[i-1] = new MapInfo(predInfoI["AVG_total_amount"], predInfoI["COUNT"], predInfoI["avg_duration"], predInfoI["AVG_trip_distance"], predInfoI["heuristic"]); // Access the properties using square brackets
        }
    }
    // console.log("Max zone is " + maxZone + " with value of " + max);
}


function numberToColor(value: number): string {
    
    // Clamp the value between 0 and 1
    const clampedValue = Math.min(1, Math.max(0, value));
    if (clampedValue==0) {
        return "#808080"
    }
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

//price - historical max 142, predictive max 61
//duration - historical max 73, predictive max 45
//distance - historical max 21, predictive max 15
//number of taxis - historical max 438, predictive max 209
//heuristic - historical max 434, predictive max 276
function updateColors() {
    let infoArray : MapInfo []=[];
    if (myModel==Model.Historical) {
        infoArray = historicalInfoArray;
    } else {
        infoArray = predictiveInfoArray;
    }
    for (let i = 0; i < mapArray.length; i++) {
        const polygon = mapArray[i];
        const zoneIndex = polygon.get("zIndex") as number;
        let number;
        if (myColor==Color.Distance) {
            number = infoArray[zoneIndex-1].getAverageTripDistance()/25.;
        } else if (myColor==Color.Duration) {
            number = infoArray[zoneIndex-1].getAverageDuration()/75.;
        } else if (myColor==Color.Number) {
            number = infoArray[zoneIndex-1].getCount()/450.;
        } else if (myColor==Color.Price) {
            number = infoArray[zoneIndex-1].getAverageTotalAmount()/150.;
        } else if (myColor==Color.Heuristic) {
            number = infoArray[zoneIndex-1].getHeuristic()/450.;
        }
        polygon.setOptions({fillColor: numberToColor(number), strokeColor: numberToColor(number)});
        polygon.setMap(null);
        polygon.setMap(map);
    }
}

 // Define a function to handle radio button changes
 function handleModelRadioButtonChange(event: Event) {
    const selectedOption = (event.target as HTMLInputElement).value;
    if (selectedOption=="historical") {
        myModel = Model.Historical;
    }
    //predictive
    else {
        myModel = Model.Predictive;
    }
    updateColors();
  }

// Define a function to handle radio button changes
function handleColorRadioButtonChange(event: Event) {
    const selectedOption = (event.target as HTMLInputElement).value;
    // Perform actions based on the selected option
    if (selectedOption=="price") {
        myColor = Color.Price;
    } else if (selectedOption=="duration") {
        myColor = Color.Duration;
    } else if (selectedOption=="taxis") {
        myColor = Color.Number;
    } else if (selectedOption=="distance") {
        myColor = Color.Distance;
    } else if (selectedOption=="heuristic") {
        myColor = Color.Heuristic;
    }
    updateColors();
  }

  // Get a reference to the radio buttons
  const modelButtons = document.querySelectorAll<HTMLInputElement>('input[name="model"]');
  
  // Attach event listeners to the radio buttons
  modelButtons.forEach(button => {
    button.addEventListener('change', handleModelRadioButtonChange);
  });
  
  // Get a reference to the radio buttons
  const radioButtons = document.querySelectorAll<HTMLInputElement>('input[name="color-option"]');
  
  // Attach event listeners to the radio buttons
  radioButtons.forEach(button => {
    button.addEventListener('change', handleColorRadioButtonChange);
  });

  

 