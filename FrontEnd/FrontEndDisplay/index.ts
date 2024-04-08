import axios from 'axios';
import { getMapArr } from './mapData'; // Import the mapData module

let mapArray: google.maps.Polygon[] = [];
const zoneInfoArray: MapInfo []=[];
let map: google.maps.Map;


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
      this.heuristic = Math.log(heuristic)/3;
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

    

    for (let i=0; i<262; i++) {
        zoneInfoArray.push(new MapInfo(0, 0, 0, 0, 0));
    }

    mapArray = getMapArr();
    //BEGINNING OF ADDED STUFF
    let currentInfoWindow: google.maps.InfoWindow | null = null;

    for (let i = 0; i < mapArray.length; i++) {
        const polygon = mapArray[i];
        polygon.setMap(map);
        const infoWindow = new google.maps.InfoWindow({
            content: "<div id='infoContent'>Zone Num: " + polygon.get("zIndex") + "\n</div>",
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
                Zone Num: ${zoneIndex} <br>
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

  
}

// Call initMap when the page loads
window.addEventListener("load", () => {
  initMap();
});

//Average total amount (dollars and cents)
//Count (amount of trips picked up in 30 minute time frame)
//Average Duration (minutes)
//Average trip distance (miles)
//Heuristic (total amount/duration)

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
    for (let i=1; i<=262; i++) {
        const infoI = response[i]; // Access the property directly from the object
        if (!infoI) {
            console.log('Error ' + i);
        }
        else {
            zoneInfoArray[i-1] = new MapInfo(infoI["AVG_total_amount"], infoI["COUNT"], infoI["avg_duration"], infoI["AVG_trip_distance"], infoI["heuristic"]); // Access the properties using square brackets
        }
    }
}


function numberToColor(value: number): string {
    // Ensure the value is within the [0, 1] range
    value = Math.max(0, Math.min(1, value));

    // Define the color stops for the gradient
    const colors = [
        { value: 0, color: [255, 0, 0] },     // Red
        { value: 0.5, color: [255, 165, 0] }, // Orange
        { value: 0.75, color: [255, 255, 0] },// Yellow
        { value: 1, color: [0, 255, 0] }      // Green
    ];

    // Special case for value 0
    if (value == 0) {
        return '#' + colors[0].color.map(c => c.toString(16).padStart(2, '0')).join('');
    }

    // Special case for value 1
    if (value == 1) {
        return '#' + colors[colors.length - 1].color.map(c => c.toString(16).padStart(2, '0')).join('');
    }

    // Find the two closest color stops for the value
    let i = 0;
    while (value > colors[i].value) {
        i++;
    }

    // Interpolate between the two closest colors
    const ratio = (value - colors[i - 1].value) / (colors[i].value - colors[i - 1].value);
    const color = [
        Math.round(colors[i - 1].color[0] + ratio * (colors[i].color[0] - colors[i - 1].color[0])),
        Math.round(colors[i - 1].color[1] + ratio * (colors[i].color[1] - colors[i - 1].color[1])),
        Math.round(colors[i - 1].color[2] + ratio * (colors[i].color[2] - colors[i - 1].color[2]))
    ];

    // Convert RGB components to hex format
    const hexColor = '#' + color.map(c => c.toString(16).padStart(2, '0')).join('');

    return hexColor;
}

function updateColors() {
    
    for (let i = 0; i < mapArray.length; i++) {
        const polygon = mapArray[i];
        const zoneIndex = polygon.get("zIndex") as number;
        if (zoneIndex>240) {
            alert("zone " + zoneIndex);
        }
        polygon.setOptions({fillColor: numberToColor(zoneInfoArray[zoneIndex-1].getHeuristic()), strokeColor: numberToColor(zoneInfoArray[zoneIndex-1].getHeuristic())});
        polygon.setMap(null);
        polygon.setMap(map);
    }
}
