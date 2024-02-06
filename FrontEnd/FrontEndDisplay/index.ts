function initMap(): void {
  const map = new google.maps.Map(document.getElementById("map") as HTMLElement, {
    zoom: 9.5,
    center: { lat: 40.771019, lng: -73.958722 }
  });


  // Define the LatLng coordinates for the polygon's path.
  const triangleCoords = [
    { lat: 25.774, lng: -80.19 },
    { lat: 18.466, lng: -66.118 },
    { lat: 32.321, lng: -64.757 },
    { lat: 25.774, lng: -80.19 },
  ];

  // Construct the polygon.
  const bermudaTriangle = new google.maps.Polygon({
    paths: triangleCoords,
    strokeColor: "#FF0000",
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: "#FF0000",
    fillOpacity: 0.35,
  });

  bermudaTriangle.setMap(map);
}

// Call initMap when the page loads
window.addEventListener("load", () => {
  initMap();
});
