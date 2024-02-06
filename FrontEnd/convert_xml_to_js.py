import xml.etree.ElementTree as ET
import sys

def extract_coordinates(coordinates_text):
    # Extract coordinates without the extra characters at the end
    coordinates_list = coordinates_text.split('((')[1].split('))')[0].split(', ')
    
    # Clean up the coordinates by removing leading and trailing parentheses
    coordinates_list = [point.replace('(', '').replace(')', '') for point in coordinates_list]
    
    coordinates = [tuple(map(float, point.split())) for point in coordinates_list]
    return coordinates

def format_coordinates_for_js(coordinates):
    # Format coordinates in the desired JavaScript format
    return [f'{{ lat: {lat}, lng: {lng} }}' for lng, lat in coordinates]

def convertXMLtoJavaScript(xml_file_path, js_file_path):
    with open(xml_file_path, 'r') as xml_file:
        xml_data = xml_file.read()

    root = ET.fromstring(xml_data)
    locations = root.findall('.//row')

    with open(js_file_path, 'w') as js_file:
        for location in locations:
            location_id_element = location.find('location_id')
            
            if location_id_element is not None:
                location_id = location_id_element.text

                coordinates_text = location.find('.//the_geom').text
                coordinates = extract_coordinates(coordinates_text)
                formatted_coordinates = format_coordinates_for_js(coordinates)

                javascript_code = f'''
const coordsZone{location_id} = [
    {', '.join(formatted_coordinates)}
];

const zone{location_id} = new google.maps.Polygon({{
    paths: coordsZone{location_id},
    strokeColor: "#FF0000",
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: "#FF0000",
    fillOpacity: 0.35,
}});

zone{location_id}.setMap(map);
'''

                js_file.write(javascript_code + '\n')

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script.py input_xml_file output_js_file")
        sys.exit(1)

    input_xml_file = sys.argv[1]
    output_js_file = sys.argv[2]

    convertXMLtoJavaScript(input_xml_file, output_js_file)
