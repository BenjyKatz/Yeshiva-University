
import pandas as pd
from sklearn.model_selection import train_test_split

df = pd.read_csv('aug_train.csv')

#X = df.drop('target', axis=1)
X = df
y = df['target']

# Split the data into a training set (usually 70-80%) and a testing set (usually 20-30%)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

hired = X_train[X_train['target']==1]
reject = X_train[X_train['target']==0]

def get_score(candidate):
  points = 0
  if(candidate['city_development_index']<0.65):
    points = points+7
  if(candidate['relevent_experience']=='No relevent experience'):
    points = points+2
  if(candidate['enrolled_university']=='Full time course'):
    points = points+3
  if(candidate['education_level']=='Graduate'):
    points = points+1
  if(candidate['major_discipline']=='STEM'):
    points = points+1
  #print(points)
  return points

def reach_out(dataFrame):
	predictions = (dataFrame.apply(get_score, axis=1)>5)
	scores = (dataFrame.apply(get_score, axis=1)/14)
	return scores