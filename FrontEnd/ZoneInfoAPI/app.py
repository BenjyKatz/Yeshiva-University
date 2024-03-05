from flask import Flask, request, jsonify
from datetime import timedelta
from datetime import datetime
from flask_cors import CORS

from sodapy import Socrata
import pandas as pd

app = Flask(__name__)
CORS(app)


from datetime import datetime
def break_up_date(date_obj):


  # Get week of the year
  week_of_year = date_obj.isocalendar()[1]

  # Get day of the week (Monday is 1 and Sunday is 7)
  day_of_week = date_obj.isoweekday()

  return week_of_year, day_of_week


def get_old_date(year, week_of_year, day_of_week):
  # Convert to date
  date_obj = datetime.fromisocalendar(year, week_of_year, day_of_week)

  # Convert date object to string
  date_string = date_obj.strftime('%Y-%m-%d')
  print("Date:", date_string)
  return date_obj

def get_data(start_time, end_time):
    # Unauthenticated client only works with public data sets. Note 'None'
    # in place of application token, and no username or password:


    # Example authenticated client (needed for non-public datasets):
    client = Socrata("data.cityofnewyork.us",
                    "u00Cxr2BfQIdhyzmtP61ghu8t",
                    username="bkatz7@mail.yu.edu",
                    password="Capstone2024")

    # First 2000 results, returned as JSON from API / converted to Python list of
    # dictionaries by sodapy.
    query_params = {
        "select": "pulocationid, AVG(total_amount), COUNT(*),  AVG( (date_extract_hh(tpep_dropoff_datetime)- date_extract_hh(tpep_pickup_datetime))*60 +(date_extract_mm(tpep_dropoff_datetime)-date_extract_mm(tpep_pickup_datetime))) AS avg_duration,"+
        " AVG(trip_distance)",
        "where": f"tpep_pickup_datetime >= '{start_time}' AND tpep_pickup_datetime < '{end_time}'",

        "group": "pulocationid",
        "order": "AVG_total_amount"
        #"limit": 40000
    }

    # Send the query and get the results
    results = client.get("biws-g3hs", **query_params)
    # print(results)


    # Convert to pandas DataFrame
    results_df = pd.DataFrame.from_records(results)

    return results_df
@app.route('/historical', methods=['GET'])
def historical():
    # Get the datetime parameter from the query string
    print(request.args)
    datetime_param = request.args.get('datetime')

    # Convert string to datetime object
    date_obj = datetime.fromisoformat(datetime_param)
    time_obj = date_obj.time()
    week, day = break_up_date(date_obj)
    old_date = get_old_date(2017, week, day)
    look_up_date = datetime.combine(old_date, time_obj)
    print(look_up_date)

    
    start_time = look_up_date - timedelta(minutes=15)
    end_time = look_up_date + timedelta(minutes=15)
    start_time = start_time.strftime('%Y-%m-%dT%H:%M:%S')
    end_time = end_time.strftime('%Y-%m-%dT%H:%M:%S')
    print(start_time)
    print(end_time)

    data = get_data(start_time, end_time)



    # Return the response as JSON
    data.set_index('pulocationid', inplace = True)
    print(data)
    return data.to_json(orient='index')

if __name__ == '__main__':
    app.run(debug=True)