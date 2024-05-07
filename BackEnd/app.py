from flask import Flask, request, jsonify
from datetime import timedelta
from datetime import datetime
from flask_cors import CORS
from tensorflow.keras.models import load_model
import keras
import tensorflow as tf
import numpy as np

from tensorflow.keras.losses import mse
from sklearn.metrics import mean_squared_error


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
        "select": "pulocationid, AVG(total_amount), COUNT(*),  AVG( (date_extract_hh(tpep_dropoff_datetime)- date_extract_hh(tpep_pickup_datetime))*60 +(date_extract_mm(tpep_dropoff_datetime)-date_extract_mm(tpep_pickup_datetime))) AS avg_duration, CASE WHEN AVG_total_amount = 0 OR avg_duration = 0 THEN 0 ELSE AVG_total_amount / avg_duration * count END AS heuristic,"+
        " AVG(trip_distance)",
        "where": f"tpep_pickup_datetime >= '{start_time}' AND tpep_pickup_datetime < '{end_time}'",

        "group": "pulocationid",
        "order": "pulocationid"
    }

    # Send the query and get the results
    # results = client.get("biws-g3hs", **query_params)
    results = client.get("qp3b-zxtp", **query_params)
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
    old_date = get_old_date(2022, week, day)
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

def calculate_day_interval(hour, minute):
    total_minutes = hour * 60 + minute  # Convert hours to minutes and add the minutes
    half_hour_intervals = total_minutes // 30  # Divide by 30 to get half-hour intervals
    rounded_interval = round(half_hour_intervals)  # Round the result to the nearest integer
    return rounded_interval

@app.route('/predictive', methods=['GET'])
def predictive():
    # Get the datetime parameter from the query string
    print(request.args)
    datetime_param = request.args.get('datetime')

    # Convert string to datetime object
    date_obj = datetime.fromisoformat(datetime_param)
    time_obj = date_obj.time()
    week, day = break_up_date(date_obj)
    old_date = get_old_date(2022, week, day)
    look_up_date = datetime.combine(old_date, time_obj)
    print(look_up_date)

    
    start_time = look_up_date - timedelta(minutes=15)
    end_time = look_up_date + timedelta(minutes=15)
    start_time = start_time.strftime('%Y-%m-%dT%H:%M:%S')
    end_time = end_time.strftime('%Y-%m-%dT%H:%M:%S')


    print(start_time)
    print(end_time)

    result = get_data(start_time, end_time)
    # Get the day of the week (Monday is 0 and Sunday is 6)
    day_of_week = date_obj.weekday()
    hour = date_obj.hour
    minute = date_obj.minute
    day_interval = calculate_day_interval(hour, minute)


    # Get the ISO calendar week of the year and year
    year, week_of_year, _ = date_obj.isocalendar()


    #X = result[["pulocationid"]].astype(int)
    X = result
    X = X.assign(dow = day_of_week)
    X = X.assign(woy = week_of_year)
    X = X.assign(year = year)
    X = X.assign(day_interval = day_interval)

    #X = result[["dow"]].astype(int)
    
    all_dow = ["dow_" + str(i) for i in range(0,7)]
    X_dow_encoded = pd.DataFrame(0, index=X.index, columns=all_dow)
    # Update values based on the actual data in each batch
    for dow in all_dow:
        if int(dow[4:]) in X['dow'].unique():
            X_dow_encoded[dow] = (X['dow'] == int(dow[4:])).astype(int)


    X = pd.concat([X, X_dow_encoded], axis=1)
    X = X.drop(["heuristic" ], axis = 1)
    # Define the desired column order
    desired_columns_order = ['pulocationid', 'woy', 'dow', 'year','day_interval','AVG_total_amount', 'COUNT', 'avg_duration','AVG_trip_distance','dow_0','dow_1','dow_2','dow_3','dow_4','dow_5','dow_6']  # Example column order

    # Reassign the DataFrame with the desired column order using .loc
    X = X.loc[:, desired_columns_order]

    # Return the response as JSON
    #X.set_index('pulocationid', inplace = True)

    model = load_model('taxi_model_2018-04-06_Month.h5', custom_objects={'mse': 'mse'}) 


    X_tensor = tf.convert_to_tensor(X.values, dtype=tf.float32)
    # Evaluate the model on the test data
    y_pred = model.predict(X_tensor)
    

    ids_column = X['pulocationid'].values.reshape(-1, 1)  # Extract the IDs column from X

    # Concatenate the IDs column with the predictions array
    y_pred_with_ids = np.concatenate([ids_column, y_pred], axis=1)
    predicitions = pd.DataFrame(y_pred_with_ids, columns = ['pulocationid','AVG_total_amount', 'COUNT', 'avg_duration', 'AVG_trip_distance'], index = X.index)
    predicitions['heuristic'] = np.where(predicitions['avg_duration'] != 0, (predicitions['AVG_total_amount'] / predicitions['avg_duration']) * predicitions['COUNT'], 0)


    predicitions.set_index('pulocationid', inplace = True)
    print(predicitions)
 
    return predicitions.to_json(orient='index')

if __name__ == '__main__':
    app.run(debug=True)