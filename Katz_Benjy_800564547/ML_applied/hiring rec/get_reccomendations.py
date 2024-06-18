
from hire_model import reach_out
import pandas as pd
import sys
file_path = sys.argv[1]
df = pd.read_csv(file_path)
scores = reach_out(df)
to_hire = scores>.6
df['scores'] = scores
df['hire?'] = to_hire
df.to_csv('output_hire_recs.csv', index = False);
print('check output_hire_recs.csv')
